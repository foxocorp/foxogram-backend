package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.MessageCreateDTO;
import su.foxogram.dtos.response.MessageDTO;
import su.foxogram.dtos.response.MessagesDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.MessageNotFoundException;
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
	@GetMapping("/channel/{name}")
	public List<MessageDTO> getMessages(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit) {
		return messagesService.getMessages(before, limit, channel);
	}

	@Operation(summary = "Get message")
	@GetMapping("/channel/{name}/{id}")
	public MessageDTO getMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id) throws MessageNotFoundException {
		Message message = messagesService.getMessage(id, channel);

		return new MessageDTO(message);
	}

	@Operation(summary = "Create message")
	@PostMapping("/channel/{name}")
	public OkDTO createMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @ModelAttribute MessageCreateDTO body, List<MultipartFile> attachments) throws UploadFailedException {
		messagesService.addMessage(channel, user, body, attachments);

		return new OkDTO(true);
	}

	@Operation(summary = "Delete message")
	@DeleteMapping("/channel/{name}/{id}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id) throws MessageNotFoundException, MissingPermissionsException {
		messagesService.deleteMessage(id, member, channel);

		return new OkDTO(true);
	}

	@Operation(summary = "Edit message")
	@PatchMapping("/channel/{name}/{id}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @RequestBody MessageCreateDTO body, @PathVariable long id) throws MessageNotFoundException, MissingPermissionsException {
		List<Message> message = List.of(messagesService.editMessage(id, channel, member, body));

		return new MessagesDTO(message);
	}
}
