package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.MemberConstants;
import su.foxogram.constants.StorageConstants;
import su.foxogram.dtos.request.MessageCreateDTO;
import su.foxogram.dtos.response.MessageDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.MessageNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessagesService {

	private final MessageRepository messageRepository;

	private final StorageService storageService;

	@Autowired
	public MessagesService(MessageRepository messageRepository, StorageService storageService) {
		this.messageRepository = messageRepository;
		this.storageService = storageService;
	}

	public List<MessageDTO> getMessages(long before, int limit, Channel channel) {
		List<Message> messagesArray = messageRepository.findAllByChannel(channel, limit);

		log.info("Messages ({}, {}) in channel ({}) found successfully", limit, before, channel.getId());

		return messagesArray.stream()
				.limit(limit)
				.map(MessageDTO::new)
				.collect(Collectors.toList());
	}

	public Message getMessage(long id, Channel channel) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();

		log.info("Message ({}) in channel ({}) found successfully", id, channel.getId());

		return message;
	}

	public void addMessage(Channel channel, User user, MessageCreateDTO body, List<MultipartFile> attachments) throws UploadFailedException {
		long authorId = user.getId();
		long timestamp = System.currentTimeMillis();
		List<String> uploadedAttachments = new ArrayList<>();
		String content = body.getContent();

		try {
			uploadedAttachments = attachments.stream()
					.map(attachment -> {
						try {
							return uploadAttachment(attachment);
						} catch (UploadFailedException e) {
							throw new RuntimeException(e);
						}
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new UploadFailedException();
		} finally {
			Message message = new Message(0, channel, content, authorId, timestamp, uploadedAttachments);
			messageRepository.save(message);

			log.info("Message ({}) to channel ({}) created successfully", message.getId(), channel.getId());
		}
	}

	public void deleteMessage(long id, Member member, Channel channel) throws MessageNotFoundException, MissingPermissionsException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();
		message.isAuthor(member);
		member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_MESSAGES);

		messageRepository.delete(message);
		log.info("Message ({}) in channel ({}) deleted successfully", id, channel.getId());
	}

	public Message editMessage(long id, Channel channel, Member member, MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException {
		Message message = messageRepository.findByChannelAndId(channel, id);
		String content = body.getContent();

		if (message == null) throw new MessageNotFoundException();
		message.isAuthor(member);

		message.setContent(content);
		messageRepository.save(message);
		log.info("Message ({}) in channel ({}) edited successfully", id, channel.getId());

		return message;
	}

	private String uploadAttachment(MultipartFile attachment) throws UploadFailedException {
		try {
			return storageService.uploadToMinio(attachment, StorageConstants.ATTACHMENTS_BUCKET);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UploadFailedException();
		}
	}
}
