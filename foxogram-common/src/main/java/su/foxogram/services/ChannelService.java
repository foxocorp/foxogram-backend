package su.foxogram.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.ChannelsConstants;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.constants.MemberConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.request.ChannelCreateDTO;
import su.foxogram.dtos.api.request.ChannelEditDTO;
import su.foxogram.dtos.api.response.AttachmentsDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.dtos.api.response.MemberDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.channel.ChannelAlreadyExistException;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.exceptions.member.MemberAlreadyInChannelException;
import su.foxogram.exceptions.member.MemberInChannelNotFoundException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.AttachmentsCannotBeEmpty;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.models.Attachment;
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

	private final UserService userService;

	private final RabbitService rabbitService;

	private final AttachmentService attachmentService;

	@Autowired
	public ChannelService(ChannelRepository channelRepository, MemberService memberService, UserService userService, RabbitService rabbitService, AttachmentService attachmentService) {
		this.channelRepository = channelRepository;
		this.memberService = memberService;
		this.userService = userService;
		this.rabbitService = rabbitService;
		this.attachmentService = attachmentService;
	}

	public Channel createChannel(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException, ChannelNotFoundException {
		Channel channel;

		long isPublic = 0;

		if (body.isPublic()) isPublic = ChannelsConstants.Flags.PUBLIC.getBit();

		try {
			channel = new Channel(0, body.getDisplayName(), body.getName(), isPublic, body.getType(), user);
			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new ChannelAlreadyExistException();
		}

		user = userService.getById(user.getId()).orElseThrow(ChannelNotFoundException::new);

		Member member = new Member(user, channel, MemberConstants.Permissions.ADMIN.getBit());
		memberService.add(member);

		log.debug("Channel ({}) by user ({}) created successfully", channel.getName(), user.getUsername());
		return channel;
	}

	public Channel getChannelById(long id) throws ChannelNotFoundException {
		return channelRepository.findById(id).orElseThrow(ChannelNotFoundException::new);
	}

	public Channel getChannelByName(String name) throws ChannelNotFoundException {
		Channel channel = channelRepository.findByName(name).orElseThrow(ChannelNotFoundException::new);
		if (channel.hasFlag(ChannelsConstants.Flags.PUBLIC)) return channel;
		throw new ChannelNotFoundException();
	}

	public Channel editChannel(Member member, Channel channel, ChannelEditDTO body) throws ChannelAlreadyExistException, JsonProcessingException, MissingPermissionsException, UploadFailedException {

		if (!member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_MESSAGES))
			throw new MissingPermissionsException();

		try {
			if (body.getDisplayName() != null) channel.setDisplayName(body.getDisplayName());
			if (body.getName() != null) channel.setName(body.getName());
			if (body.getIcon() <= 0) {
				Attachment attachment = attachmentService.getById(body.getIcon());

				if (attachment == null) throw new UnknownAttachmentsException();

				channel.setIcon(attachment);
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

	public AttachmentsDTO uploadIcon(AttachmentsAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		if (attachment == null) throw new AttachmentsCannotBeEmpty();

		return attachmentService.uploadAttachment(null, attachment);
	}

	public void deleteChannel(Channel channel, User user) throws MissingPermissionsException, JsonProcessingException {
		Member member = memberService.getByChannelAndUser(channel.getId(), user.getId());

		if (!member.hasAnyPermission(MemberConstants.Permissions.ADMIN)) throw new MissingPermissionsException();

		channelRepository.delete(channel);
		rabbitService.send(getRecipients(channel), Map.of("id", channel.getId()), GatewayConstants.Event.CHANNEL_DELETE.getValue());
		log.debug("Channel ({}) deleted successfully", channel.getName());
	}

	public Member joinUser(Channel channel, User user) throws MemberAlreadyInChannelException, JsonProcessingException, ChannelNotFoundException {
		Member member = memberService.getByChannelAndUser(channel.getId(), user.getId());

		if (member != null) throw new MemberAlreadyInChannelException();

		user = userService.getById(user.getId()).orElseThrow(ChannelNotFoundException::new);

		member = new Member(user, channel, 0);
		member.setPermissions(MemberConstants.Permissions.ATTACH_FILES, MemberConstants.Permissions.SEND_MESSAGES);
		log.debug("Member ({}) joined channel ({}) successfully", member.getUser().getUsername(), channel.getName());
		rabbitService.send(getRecipients(channel), new MemberDTO(member, true), GatewayConstants.Event.MEMBER_ADD.getValue());
		return memberService.add(member);
	}

	public void leaveUser(Channel channel, User user) throws MemberInChannelNotFoundException, JsonProcessingException {
		Member member = memberService.getByChannelAndUser(channel.getId(), user.getId());
		if (member == null) throw new MemberInChannelNotFoundException();

		memberService.delete(member);
		rabbitService.send(getRecipients(channel), new MemberDTO(member, true), GatewayConstants.Event.MEMBER_REMOVE.getValue());
		log.debug("Member ({}) left channel ({}) successfully", member.getUser().getUsername(), channel.getName());
	}

	public Member getMember(Channel channel, long memberId) {
		return memberService.getByChannelAndUser(channel.getId(), memberId);
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
