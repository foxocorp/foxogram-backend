package su.foxogram.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.request.MessageCreateDTO;
import su.foxogram.dtos.api.response.AttachmentsDTO;
import su.foxogram.dtos.api.response.MessageDTO;
import su.foxogram.dtos.api.response.MessagesDTO;
import su.foxogram.dtos.api.response.OkDTO;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.AttachmentsCannotBeEmpty;
import su.foxogram.exceptions.message.MessageCannotBeEmpty;
import su.foxogram.exceptions.message.MessageNotFoundException;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.services.MessagesService;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Messages")
@RequestMapping(value = APIConstants.MESSAGES, produces = "application/json")
public class MessagesController {

	private final MessagesService messagesService;

	@Autowired
	public MessagesController(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	@Operation(summary = "Get messages")
	@GetMapping("/channel/{id}")
	public List<MessageDTO> getMessages(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @RequestParam(defaultValue = "0") long before, @RequestParam(defaultValue = "25") int limit) {
		if (before <= 0) {
			before = System.currentTimeMillis();
		}

		if (limit <= 0) {
			limit = 25;
		}

		return messagesService.getMessages(before, limit, channel);
	}

	@Operation(summary = "Get message")
	@GetMapping("/channel/{id}/{messageId}")
	public MessageDTO getMessage(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @PathVariable long messageId) throws MessageNotFoundException {
		return messagesService.getMessage(messageId, channel);
	}

	@Operation(summary = "Create message")
	@PostMapping("/channel/{id}")
	public MessageDTO createMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @RequestBody @Valid MessageCreateDTO body) throws JsonProcessingException, MessageCannotBeEmpty, MissingPermissionsException, UnknownAttachmentsException {
		if (body.getContent().isBlank()) {
			throw new MessageCannotBeEmpty();
		}

		Message message = messagesService.addMessage(channel, user, body);

		return new MessageDTO(message, null, true);
	}

	@Operation(summary = "Add attachments")
	@PutMapping("/channel/{id}/attachments")
	public List<AttachmentsDTO> addAttachments(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @RequestBody List<AttachmentsAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty {
		if (attachments == null || attachments.isEmpty()) {
			throw new AttachmentsCannotBeEmpty();
		}

		return messagesService.addAttachments(channel, user, attachments);
	}

	@Operation(summary = "Delete message")
	@DeleteMapping("/channel/{id}/{messageId}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @PathVariable long messageId) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException {
		messagesService.deleteMessage(messageId, member, channel);

		return new OkDTO(true);
	}

	@Operation(summary = "Edit message")
	@PatchMapping("/channel/{id}/{messageId}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @PathVariable long messageId, @Valid @RequestBody MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException {
		List<Message> message = List.of(messagesService.editMessage(messageId, channel, member, body));

		return new MessagesDTO(message);
	}
}
