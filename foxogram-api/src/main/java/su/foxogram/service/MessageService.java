package su.foxogram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constant.GatewayConstant;
import su.foxogram.constant.MemberConstant;
import su.foxogram.dto.api.request.AttachmentAddDTO;
import su.foxogram.dto.api.request.MessageCreateDTO;
import su.foxogram.dto.api.response.MessageDTO;
import su.foxogram.dto.api.response.UploadAttachmentDTO;
import su.foxogram.exception.channel.ChannelNotFoundException;
import su.foxogram.exception.member.MemberInChannelNotFoundException;
import su.foxogram.exception.member.MissingPermissionsException;
import su.foxogram.exception.message.AttachmentsCannotBeEmpty;
import su.foxogram.exception.message.MessageNotFoundException;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.model.*;
import su.foxogram.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageService {

	private final MessageRepository messageRepository;

	private final RabbitService rabbitService;

	private final ChannelService channelService;

	private final MemberService memberService;

	private final AttachmentService attachmentService;

	@Autowired
	public MessageService(MessageRepository messageRepository, RabbitService rabbitService, ChannelService channelService, MemberService memberService, AttachmentService attachmentService) {
		this.messageRepository = messageRepository;
		this.rabbitService = rabbitService;
		this.channelService = channelService;
		this.memberService = memberService;
		this.attachmentService = attachmentService;
	}

	public List<Message> getAllByChannel(long before, int limit, Channel channel) {
		List<Message> messagesArray = messageRepository.findAllByChannel(channel, before, limit);

		log.debug("Messages ({}, {}) in channel ({}) found successfully", limit, before, channel.getId());

		return messagesArray.reversed();
	}

	public Message getByIdAndChannel(long id, Channel channel) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id).orElseThrow(MessageNotFoundException::new);

		log.debug("Message ({}) in channel ({}) found successfully", id, channel.getId());

		return message;
	}

	public Message add(Channel channel, User user, MessageCreateDTO body) throws JsonProcessingException, MissingPermissionsException, UnknownAttachmentsException, ChannelNotFoundException, MemberInChannelNotFoundException {
		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).orElseThrow(MemberInChannelNotFoundException::new);

		if (!member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.SEND_MESSAGES))
			throw new MissingPermissionsException();

		List<Attachment> attachments = new ArrayList<>();
		if (body.getAttachments() != null) attachments = attachmentService.get(user, body.getAttachments());

		Message message = new Message(channel, body.getContent(), member, attachments);
		messageRepository.save(message);

		rabbitService.send(getRecipients(channel), new MessageDTO(message, true), GatewayConstant.Event.MESSAGE_CREATE.getValue());
		log.debug("Message ({}) to channel ({}) created successfully", message.getId(), channel.getId());

		return message;
	}

	public List<UploadAttachmentDTO> addAttachments(Channel channel, User user, List<AttachmentAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty, MemberInChannelNotFoundException {
		if (attachments.isEmpty()) throw new AttachmentsCannotBeEmpty();

		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).orElseThrow(MemberInChannelNotFoundException::new);

		if (!member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.SEND_MESSAGES))
			throw new MissingPermissionsException();

		return attachmentService.uploadAll(user, attachments);
	}

	public void delete(long id, Member member, Channel channel) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id).orElseThrow(MessageNotFoundException::new);

		if (!message.isAuthor(member) && !member.hasAnyPermission(MemberConstant.Permissions.ADMIN, MemberConstant.Permissions.MANAGE_MESSAGES))
			throw new MissingPermissionsException();

		messageRepository.delete(message);
		rabbitService.send(getRecipients(channel), Map.of("id", id), GatewayConstant.Event.MESSAGE_DELETE.getValue());
		log.debug("Message ({}) in channel ({}) deleted successfully", id, channel.getId());
	}

	public Message update(long id, Channel channel, Member member, MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id).orElseThrow(MessageNotFoundException::new);
		String content = body.getContent();

		if (!message.isAuthor(member)) throw new MissingPermissionsException();

		message.setContent(content);
		messageRepository.save(message);

		rabbitService.send(getRecipients(channel), new MessageDTO(message, true), GatewayConstant.Event.MESSAGE_UPDATE.getValue());
		log.debug("Message ({}) in channel ({}) edited successfully", id, channel.getId());

		return message;
	}

	private List<Long> getRecipients(Channel channel) throws ChannelNotFoundException {
		return channelService.getById(channel.getId())
				.getMembers().stream()
				.map(Member::getUser)
				.map(User::getId)
				.collect(Collectors.toList());
	}

	public Message getLastByChannel(Channel channel) {
		return messageRepository.getLastMessageByChannel(channel).orElse(null);
	}
}
