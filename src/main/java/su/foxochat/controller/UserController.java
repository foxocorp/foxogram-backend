package su.foxochat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxochat.constant.APIConstant;
import su.foxochat.constant.AttributeConstant;
import su.foxochat.dto.api.request.AttachmentAddDTO;
import su.foxochat.dto.api.request.OTPDTO;
import su.foxochat.dto.api.request.UserDeleteDTO;
import su.foxochat.dto.api.request.UserEditDTO;
import su.foxochat.dto.api.response.ChannelDTO;
import su.foxochat.dto.api.response.OkDTO;
import su.foxochat.dto.api.response.UploadAttachmentDTO;
import su.foxochat.dto.api.response.UserDTO;
import su.foxochat.dto.internal.AttachmentPresignedDTO;
import su.foxochat.exception.message.AttachmentsCannotBeEmpty;
import su.foxochat.exception.message.UnknownAttachmentsException;
import su.foxochat.exception.otp.OTPExpiredException;
import su.foxochat.exception.otp.OTPsInvalidException;
import su.foxochat.exception.user.UserContactAlreadyExistException;
import su.foxochat.exception.user.UserContactNotFoundException;
import su.foxochat.exception.user.UserCredentialsIsInvalidException;
import su.foxochat.exception.user.UserNotFoundException;
import su.foxochat.model.Channel;
import su.foxochat.model.Message;
import su.foxochat.model.User;
import su.foxochat.service.AttachmentService;
import su.foxochat.service.MemberService;
import su.foxochat.service.MessageService;
import su.foxochat.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "Users")
@RequestMapping(value = APIConstant.USERS, produces = "application/json")
public class UserController {

	private final UserService userService;

	private final MemberService memberService;

	private final MessageService messageService;

	private final AttachmentService attachmentService;

	public UserController(UserService userService, MemberService memberService, MessageService messageService, AttachmentService attachmentService) {
		this.userService = userService;
		this.memberService = memberService;
		this.messageService = messageService;
		this.attachmentService = attachmentService;
	}

	@Operation(summary = "Get me")
	@GetMapping("/@me")
	public UserDTO getMe(@RequestAttribute(value = AttributeConstant.USER) User user) {
		List<Long> channels = memberService.getChannelsByUserId(user.getId())
				.stream()
				.map(Channel::getId)
				.collect(Collectors.toList());

		List<Long> contacts = user.getContacts().stream().map(userContact -> userContact.getContact().getId()).toList();

		return new UserDTO(user, channels, contacts, true, true, true);
	}

	@Operation(summary = "Get user by id")
	@GetMapping("/{id}")
	public UserDTO getById(@PathVariable long id) throws UserNotFoundException {
		return new UserDTO(userService.getById(id).orElseThrow(UserNotFoundException::new),
				null,
				null, false,
				false, false);
	}

	@Operation(summary = "Get user by username")
	@GetMapping("/@{username}")
	public UserDTO getByUsername(@PathVariable String username) throws UserNotFoundException {
		return new UserDTO(userService.getByUsername(username).orElseThrow(UserNotFoundException::new), null, null, false, false, false);
	}

	@Operation(summary = "Get user channels")
	@GetMapping("/@me/channels")
	public List<ChannelDTO> getChannels(@RequestAttribute(value = AttributeConstant.USER) User authenticatedUser) {
		return memberService.getChannelsByUserId(authenticatedUser.getId())
				.stream()
				.map(channel -> {
					Message lastMessage = messageService.getLastByChannel(channel);
					return new ChannelDTO(channel, lastMessage);
				})
				.collect(Collectors.toList());
	}

	@Operation(summary = "Edit user")
	@PatchMapping("/@me")
	public UserDTO edit(@RequestAttribute(value = AttributeConstant.USER) User authenticatedUser, @RequestBody UserEditDTO body) throws Exception {
		authenticatedUser = userService.update(authenticatedUser, body);

		return new UserDTO(authenticatedUser, null, null, true, true, false);
	}

	@Operation(summary = "Upload avatar")
	@PutMapping("/@me/avatar")
	public UploadAttachmentDTO uploadAvatar(@RequestAttribute(value = AttributeConstant.USER) User authenticatedUser, @RequestBody AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		AttachmentPresignedDTO data = attachmentService.upload(authenticatedUser, attachment);

		return new UploadAttachmentDTO(data.getUrl(), data.getAttachment().getId());
	}

	@Operation(summary = "Delete")
	@DeleteMapping("/@me")
	public OkDTO delete(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestBody UserDeleteDTO body) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();

		userService.requestDelete(user, password);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm delete")
	@PostMapping("/@me/delete-confirm")
	public OkDTO deleteConfirm(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestBody OTPDTO body) throws OTPExpiredException, OTPsInvalidException {
		userService.confirmDelete(user, body.getOTP());

		return new OkDTO(true);
	}

	@Operation(summary = "Get contacts")
	@GetMapping("/@me/contacts")
	public List<UserDTO> getContacts(@RequestAttribute(value = AttributeConstant.USER) User authenticatedUser) throws UserNotFoundException {
		User user = userService.getById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
		return user.getContacts()
				.stream()
				.map(contact -> new UserDTO(contact.getContact(), null, null, false, false, false))
				.collect(Collectors.toList());
	}

	@Operation(summary = "Add contact")
	@PostMapping("/{id}")
	public UserDTO addContact(@RequestAttribute(value = AttributeConstant.USER) User user, @PathVariable long id) throws UserContactAlreadyExistException {
		return new UserDTO(userService.addContact(user, id), null, null, false, false, false);
	}

	@Operation(summary = "Delete contact")
	@DeleteMapping("/{id}")
	public OkDTO deleteContact(@RequestAttribute(value = AttributeConstant.USER) User user, @PathVariable long id) throws UserContactNotFoundException {
		userService.deleteContact(user, id);

		return new OkDTO(true);
	}
}
