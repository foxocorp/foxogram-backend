package app.foxochat.service.impl;

import app.foxochat.constant.GatewayConstant;
import app.foxochat.constant.MemberConstant;
import app.foxochat.dto.api.request.AttachmentAddDTO;
import app.foxochat.dto.api.request.MessageCreateDTO;
import app.foxochat.dto.api.response.MessageDTO;
import app.foxochat.dto.api.response.UploadAttachmentDTO;
import app.foxochat.exception.channel.ChannelNotFoundException;
import app.foxochat.exception.member.MemberInChannelNotFoundException;
import app.foxochat.exception.member.MissingPermissionsException;
import app.foxochat.exception.message.AttachmentsCannotBeEmpty;
import app.foxochat.exception.message.MessageNotFoundException;
import app.foxochat.model.*;
import app.foxochat.repository.MessageRepository;
import app.foxochat.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;

	private final GatewayService gatewayService;

	private final MemberService memberService;

	private final AttachmentService attachmentService;

	private final ChannelService channelService;

	public MessageServiceImpl(MessageRepository messageRepository, GatewayService gatewayService, MemberService memberService, AttachmentService attachmentService, ChannelService channelService) {
		this.messageRepository = messageRepository;
		this.gatewayService = gatewayService;
		this.memberService = memberService;
		this.attachmentService = attachmentService;
		this.channelService = channelService;
	}

	@Override
	public List<Message> getAllByChannel(long before, int limit, Channel channel) {
		List<Message> messagesArray = messageRepository.findAllByChannel(channel, before, limit);

		log.debug("Messages ({}, {}) in channel ({}) found successfully", limit, before, channel.getId());

		return messagesArray;
	}

	@Override
	public Message getByIdAndChannel(long id, Channel channel) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id).orElseThrow(MessageNotFoundException::new);

		log.debug("Message {} in channel {} found successfully", id, channel.getId());

		return message;
	}

	@Override
	public Message add(Channel channel, User user, MessageCreateDTO body) throws Exception {
		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).orElseThrow(MemberInChannelNotFoundException::new);

		if (!member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.SEND_MESSAGES))
			throw new MissingPermissionsException();

		List<Attachment> attachments = new ArrayList<>();
		if (body.getAttachments() != null) attachments = attachmentService.get(user, body.getAttachments());

		Message message = new Message(channel, body.getContent(), member, attachments);
		messageRepository.save(message);

		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), new MessageDTO(message, true), GatewayConstant.Event.MESSAGE_CREATE.getValue());
		log.debug("Message {} to channel {} created successfully", message.getId(), channel.getId());

		return message;
	}

	@Override
	public List<UploadAttachmentDTO> addAttachments(Channel channel, User user, List<AttachmentAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty, MemberInChannelNotFoundException {
		if (attachments.isEmpty()) throw new AttachmentsCannotBeEmpty();

		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).orElseThrow(MemberInChannelNotFoundException::new);

		if (!member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.SEND_MESSAGES))
			throw new MissingPermissionsException();

		log.debug("Successfully added attachments to message {} by user {}", channel.getId(), user.getId());
		return attachmentService.uploadAll(user, attachments);
	}

	@Override
	public void delete(long id, Member member, Channel channel) throws Exception {
		Message message = messageRepository.findByChannelAndId(channel, id).orElseThrow(MessageNotFoundException::new);

		if (!message.isAuthor(member) && !member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.MANAGE_MESSAGES))
			throw new MissingPermissionsException();

		messageRepository.delete(message);
		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), Map.of("id", id, "channel_id", channel.getId()), GatewayConstant.Event.MESSAGE_DELETE.getValue());
		log.debug("Message {} in channel {} deleted successfully", id, channel.getId());
	}

	@Override
	public Message update(long id, Channel channel, Member member, MessageCreateDTO body) throws Exception {
		Message message = messageRepository.findByChannelAndId(channel, id).orElseThrow(MessageNotFoundException::new);
		String content = body.getContent();

		if (!message.isAuthor(member)) throw new MissingPermissionsException();

		message.setContent(content);
		messageRepository.save(message);

		gatewayService.sendMessageToSpecificSessions(getRecipients(channel), GatewayConstant.Opcode.DISPATCH.ordinal(), new MessageDTO(message, true), GatewayConstant.Event.MESSAGE_UPDATE.getValue());
		log.debug("Message {} in channel {} edited successfully", id, channel.getId());

		return message;
	}

	@Override
	public Message getLastByChannel(Channel channel) {
		return messageRepository.getLastMessageByChannel(channel).orElse(null);
	}

	private List<Long> getRecipients(Channel channel) throws ChannelNotFoundException {
		return channelService.getById(channel.getId())
				.getMembers().stream()
				.map(Member::getUser)
				.map(User::getId)
				.collect(Collectors.toList());
	}
}
