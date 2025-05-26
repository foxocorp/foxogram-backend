package su.foxogram.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constant.ChannelConstant;
import su.foxogram.constant.GatewayConstant;
import su.foxogram.constant.MemberConstant;
import su.foxogram.dto.api.request.ChannelCreateDTO;
import su.foxogram.dto.api.request.ChannelEditDTO;
import su.foxogram.dto.api.response.ChannelDTO;
import su.foxogram.dto.api.response.MemberDTO;
import su.foxogram.exception.cdn.UploadFailedException;
import su.foxogram.exception.channel.ChannelAlreadyExistException;
import su.foxogram.exception.channel.ChannelNotFoundException;
import su.foxogram.exception.member.MemberAlreadyInChannelException;
import su.foxogram.exception.member.MemberInChannelNotFoundException;
import su.foxogram.exception.member.MissingPermissionsException;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.model.Channel;
import su.foxogram.model.Member;
import su.foxogram.model.User;
import su.foxogram.repository.ChannelRepository;
import su.foxogram.service.AttachmentService;
import su.foxogram.service.GatewayService;
import su.foxogram.service.MemberService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChannelServiceImpl implements su.foxogram.service.ChannelService {

	private final ChannelRepository channelRepository;

	private final MemberService memberService;

	private final GatewayService gatewayService;

	private final AttachmentService attachmentService;

	public ChannelServiceImpl(ChannelRepository channelRepository, MemberService memberService, GatewayService gatewayService, AttachmentService attachmentService) {
		this.channelRepository = channelRepository;
		this.memberService = memberService;
		this.gatewayService = gatewayService;
		this.attachmentService = attachmentService;
	}

	@Override
	public Channel add(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException {
		Channel channel;

		long isPublic = 0;

		if (body.isPublic() && body.getType() != ChannelConstant.Type.DM.getType())
			isPublic = ChannelConstant.Flags.PUBLIC.getBit();

		try {
			channel = new Channel(body.getDisplayName(), body.getName(), isPublic, body.getType(), user);
			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new ChannelAlreadyExistException();
		}

		Member member = new Member(user, channel, MemberConstant.Permissions.ADMIN.getBit());
		memberService.add(member);

		log.debug("Channel ({}) by user ({}) created successfully", channel.getName(), user.getUsername());
		return channel;
	}

	@Override
	public Channel getById(long id) throws ChannelNotFoundException {
		return channelRepository.findById(id).orElseThrow(ChannelNotFoundException::new);
	}

	@Override
	public Channel getByName(String name) throws ChannelNotFoundException {
		Channel channel = channelRepository.findByName(name).orElseThrow(ChannelNotFoundException::new);
		if (channel.hasFlag(ChannelConstant.Flags.PUBLIC)) return channel;
		throw new ChannelNotFoundException();
	}

	@Override
	public Channel update(Member member, Channel channel, ChannelEditDTO body) throws Exception {
		if (!member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.MANAGE_MESSAGES))
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

		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), new ChannelDTO(channel, null), GatewayConstant.Event.CHANNEL_UPDATE.getValue());
		log.debug("Channel ({}) edited successfully", channel.getName());
		return channel;
	}

	@Override
	public void delete(Channel channel, User user) throws Exception {
		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId())
				.orElseThrow(MemberInChannelNotFoundException::new);

		if (!member.hasAnyPermission(MemberConstant.Permissions.ADMIN)) throw new MissingPermissionsException();

		channelRepository.delete(channel);
		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), Map.of("id", channel.getId()), GatewayConstant.Event.CHANNEL_DELETE.getValue());
		log.debug("Channel ({}) deleted successfully", channel.getName());
	}

	@Override
	public Member addMember(Channel channel, User user) throws Exception {
		// check if member not exist in channel
		if (memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).isPresent())
			throw new MemberAlreadyInChannelException();

		Member member = new Member(user, channel, 0);
		member.setPermissions(MemberConstant.Permissions.ATTACH_FILES, MemberConstant.Permissions.SEND_MESSAGES);
		log.debug("Member ({}) joined channel ({}) successfully", member.getUser().getUsername(), channel.getName());
		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), new MemberDTO(member, true), GatewayConstant.Event.MEMBER_ADD.getValue());
		return memberService.add(member);
	}

	@Override
	public void removeMember(Channel channel, User user) throws Exception {
		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).orElseThrow(MemberInChannelNotFoundException::new);

		memberService.delete(member);
		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), new MemberDTO(member, true), GatewayConstant.Event.MEMBER_REMOVE.getValue());
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
