package su.foxogram.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.ChannelsConstants;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.constants.MemberConstants;
import su.foxogram.dtos.api.request.ChannelCreateDTO;
import su.foxogram.dtos.api.request.ChannelEditDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.dtos.api.response.MemberDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.channel.ChannelAlreadyExistException;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.exceptions.member.MemberAlreadyInChannelException;
import su.foxogram.exceptions.member.MemberInChannelNotFoundException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.repositories.ChannelRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChannelService {

	private final ChannelRepository channelRepository;

	private final MemberService memberService;

	private final RabbitService rabbitService;

	private final AttachmentService attachmentService;

	@Autowired
	public ChannelService(ChannelRepository channelRepository, MemberService memberService, RabbitService rabbitService, AttachmentService attachmentService) {
		this.channelRepository = channelRepository;
		this.memberService = memberService;
		this.rabbitService = rabbitService;
		this.attachmentService = attachmentService;
	}

	public Channel add(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException {
		Channel channel;

		long isPublic = 0;

		if (body.isPublic()) isPublic = ChannelsConstants.Flags.PUBLIC.getBit();

		try {
			channel = new Channel(0, body.getDisplayName(), body.getName(), isPublic, body.getType(), user);
			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new ChannelAlreadyExistException();
		}

		Member member = new Member(user, channel, MemberConstants.Permissions.ADMIN.getBit());
		memberService.add(member);

		log.debug("Channel ({}) by user ({}) created successfully", channel.getName(), user.getUsername());
		return channel;
	}

	public Channel getById(long id) throws ChannelNotFoundException {
		return channelRepository.findById(id).orElseThrow(ChannelNotFoundException::new);
	}

	public Channel getByName(String name) throws ChannelNotFoundException {
		Channel channel = channelRepository.findByName(name).orElseThrow(ChannelNotFoundException::new);
		if (channel.hasFlag(ChannelsConstants.Flags.PUBLIC)) return channel;
		throw new ChannelNotFoundException();
	}

	public Channel update(Member member, Channel channel, ChannelEditDTO body) throws ChannelAlreadyExistException, JsonProcessingException, MissingPermissionsException, UploadFailedException {
		if (!member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_MESSAGES))
			throw new MissingPermissionsException();

		try {
			if (body.getDisplayName() != null) channel.setDisplayName(body.getDisplayName());
			if (body.getName() != null) channel.setName(body.getName());
			if (body.getIcon() <= 0) {
				channel.setIcon(attachmentService.getById(body.getIcon()));
			}

			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new ChannelAlreadyExistException();
		} catch (UnknownAttachmentsException e) {
			throw new UploadFailedException();
		}

		rabbitService.send(getRecipients(channel), new ChannelDTO(channel, null), GatewayConstants.Event.CHANNEL_UPDATE.getValue());
		log.debug("Channel ({}) edited successfully", channel.getName());
		return channel;
	}

	public void delete(Channel channel, User user) throws MissingPermissionsException, JsonProcessingException, MemberInChannelNotFoundException {
		Member member = memberService.getByChannelAndUser(channel.getId(), user.getId())
				.orElseThrow(MemberInChannelNotFoundException::new);

		if (!member.hasAnyPermission(MemberConstants.Permissions.ADMIN)) throw new MissingPermissionsException();

		channelRepository.delete(channel);
		rabbitService.send(getRecipients(channel), Map.of("id", channel.getId()), GatewayConstants.Event.CHANNEL_DELETE.getValue());
		log.debug("Channel ({}) deleted successfully", channel.getName());
	}

	public Member addMember(Channel channel, User user) throws MemberAlreadyInChannelException, JsonProcessingException {
		// check if member not exist in channel
		if (memberService.getByChannelAndUser(channel.getId(), user.getId()).isPresent()) throw new MemberAlreadyInChannelException();

		Member member = new Member(user, channel, 0);
		member.setPermissions(MemberConstants.Permissions.ATTACH_FILES, MemberConstants.Permissions.SEND_MESSAGES);
		log.debug("Member ({}) joined channel ({}) successfully", member.getUser().getUsername(), channel.getName());
		rabbitService.send(getRecipients(channel), new MemberDTO(member, true), GatewayConstants.Event.MEMBER_ADD.getValue());
		return memberService.add(member);
	}

	public void removeMember(Channel channel, User user) throws MemberInChannelNotFoundException, JsonProcessingException {
		Member member = memberService.getByChannelAndUser(channel.getId(), user.getId()).orElseThrow(MemberInChannelNotFoundException::new);

		memberService.delete(member);
		rabbitService.send(getRecipients(channel), new MemberDTO(member, true), GatewayConstants.Event.MEMBER_REMOVE.getValue());
		log.debug("Member ({}) left channel ({}) successfully", member.getUser().getUsername(), channel.getName());
	}

	private List<Long> getRecipients(Channel channel) {
		Optional<Channel> optChannel = channelRepository.findById(channel.getId());

		if (optChannel.isPresent()) channel = optChannel.get();

		return channel.getMembers().stream()
				.map(Member::getUser)
				.map(User::getId)
				.collect(Collectors.toList());
	}
}
