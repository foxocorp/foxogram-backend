package su.foxogram.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.constants.MemberConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.request.MessageCreateDTO;
import su.foxogram.dtos.api.response.AttachmentsDTO;
import su.foxogram.dtos.api.response.MessageDTO;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.MessageNotFoundException;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.models.*;
import su.foxogram.repositories.AttachmentRepository;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessagesService {

	private final MessageRepository messageRepository;

	private final RabbitService rabbitService;

	private final ChannelRepository channelRepository;

	private final MemberRepository memberRepository;

	private final AttachmentRepository attachmentRepository;

	private final AttachmentsService attachmentsService;

	@Autowired
	public MessagesService(MessageRepository messageRepository, RabbitService rabbitService, ChannelRepository channelRepository, MemberRepository memberRepository, AttachmentRepository attachmentRepository, AttachmentsService attachmentsService) {
		this.messageRepository = messageRepository;
		this.rabbitService = rabbitService;
		this.channelRepository = channelRepository;
		this.memberRepository = memberRepository;
		this.attachmentRepository = attachmentRepository;
		this.attachmentsService = attachmentsService;
	}

	public List<MessageDTO> getMessages(long before, int limit, Channel channel) {
		List<Message> messagesArray = messageRepository.findAllByChannel(channel, before, limit);

		log.info("Messages ({}, {}) in channel ({}) found successfully", limit, before, channel.getId());

		return messagesArray.reversed().stream()
				.map(message -> {
					List<Attachment> attachments = new ArrayList<>();
					if (message.getAttachments() != null) {
						message.getAttachments().forEach(attachment -> attachments.add(attachmentRepository.findById(attachment.getId())));
					}
					return new MessageDTO(message, attachments, true);
				})
				.collect(Collectors.toList());
	}

	public MessageDTO getMessage(long id, Channel channel) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();

		List<Attachment> attachments = new ArrayList<>();
		message.getAttachments().forEach(attachment -> attachments.add(attachmentRepository.findById(attachment.getId())));

		log.info("Message ({}) in channel ({}) found successfully", id, channel.getId());

		return new MessageDTO(message, attachments, true);
	}

	public Message addMessage(Channel channel, User user, MessageCreateDTO body) throws JsonProcessingException, MissingPermissionsException, UnknownAttachmentsException, ChannelNotFoundException {
		Member member = memberRepository.findByChannelAndUser(channel, user);

		if (!member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.SEND_MESSAGES))
			throw new MissingPermissionsException();

		Message message = new Message(channel, body.getContent(), member, attachmentsService.getAttachments(user, body.getAttachments()));
		messageRepository.save(message);

		rabbitService.send(getRecipients(channel), new MessageDTO(message, null, true), GatewayConstants.Event.MESSAGE_CREATE.getValue());
		log.info("Message ({}) to channel ({}) created successfully", message.getId(), channel.getId());

		return message;
	}

	public List<AttachmentsDTO> addAttachments(Channel channel, User user, List<AttachmentsAddDTO> attachments) throws MissingPermissionsException {
		Member member = memberRepository.findByChannelAndUser(channel, user);
		if (!member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.SEND_MESSAGES))
			throw new MissingPermissionsException();

		return attachmentsService.uploadAttachments(user, attachments);
	}

	public void deleteMessage(long id, Member member, Channel channel) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();
		if (!message.isAuthor(member) && !member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_MESSAGES))
			throw new MissingPermissionsException();

		messageRepository.delete(message);
		rabbitService.send(getRecipients(channel), Map.of("id", id), GatewayConstants.Event.MESSAGE_DELETE.getValue());
		log.info("Message ({}) in channel ({}) deleted successfully", id, channel.getId());
	}

	public Message editMessage(long id, Channel channel, Member member, MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id);
		String content = body.getContent();

		if (message == null) throw new MessageNotFoundException();
		if (!message.isAuthor(member)) throw new MissingPermissionsException();

		message.setContent(content);
		messageRepository.save(message);

		rabbitService.send(getRecipients(channel), new MessageDTO(message, null, true), GatewayConstants.Event.MESSAGE_UPDATE.getValue());
		log.info("Message ({}) in channel ({}) edited successfully", id, channel.getId());

		return message;
	}

	private List<Long> getRecipients(Channel channel) throws ChannelNotFoundException {
		channel = channelRepository.findById(channel.getId()).orElseThrow(ChannelNotFoundException::new);
		return channel.getMembers().stream()
				.map(Member::getUser)
				.map(User::getId)
				.collect(Collectors.toList());
	}
}
