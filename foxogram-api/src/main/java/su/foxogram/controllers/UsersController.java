package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.request.OTPDTO;
import su.foxogram.dtos.api.request.UserDeleteDTO;
import su.foxogram.dtos.api.request.UserEditDTO;
import su.foxogram.dtos.api.response.AttachmentsDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.dtos.api.response.OkDTO;
import su.foxogram.dtos.api.response.UserDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.message.AttachmentsCannotBeEmpty;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.exceptions.otp.OTPExpiredException;
import su.foxogram.exceptions.otp.OTPsInvalidException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.exceptions.user.UserNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.services.AttachmentService;
import su.foxogram.services.MemberService;
import su.foxogram.services.MessageService;
import su.foxogram.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "Users")
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {

	private final UserService userService;

	private final MemberService memberService;

	private final MessageService messageService;

	private final AttachmentService attachmentService;

	public UsersController(UserService userService, MemberService memberService, MessageService messageService, AttachmentService attachmentService) {
		this.userService = userService;
		this.memberService = memberService;
		this.messageService = messageService;
		this.attachmentService = attachmentService;
	}

	@Operation(summary = "Get me")
	@GetMapping("/@me")
	public UserDTO getMe(@RequestAttribute(value = AttributesConstants.USER) User user) {
		List<Long> channels = memberService.getChannelsByUser(user.getId())
				.stream()
				.map(Channel::getId)
				.collect(Collectors.toList());

		return new UserDTO(user, channels, true, true);
	}

	@Operation(summary = "Get user by id")
	@GetMapping("/{id}")
	public UserDTO getById(@PathVariable long id) throws UserNotFoundException {
		return new UserDTO(userService.getById(id).orElseThrow(UserNotFoundException::new),
				null,
				false,
				false);
	}

	@Operation(summary = "Get user by username")
	@GetMapping("/@{username}")
	public UserDTO getByUsername(@PathVariable String username) throws UserNotFoundException {
		return new UserDTO(userService.getByUsername(username).orElseThrow(UserNotFoundException::new), null, false, false);
	}

	@Operation(summary = "Get user channels")
	@GetMapping("/@me/channels")
	public List<ChannelDTO> getChannels(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser) {
		return memberService.getChannelsByUser(authenticatedUser.getId())
				.stream()
				.map(channel -> {
					Message lastMessage = messageService.getLastByChannel(channel);
					return new ChannelDTO(channel, lastMessage);
				})
				.collect(Collectors.toList());
	}

	@Operation(summary = "Edit user")
	@PatchMapping("/@me")
	public UserDTO edit(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @Valid @ModelAttribute UserEditDTO body) throws UserCredentialsDuplicateException, UploadFailedException, UnknownAttachmentsException {
		authenticatedUser = userService.update(authenticatedUser, body);

		return new UserDTO(authenticatedUser, null, true, true);
	}

	@Operation(summary = "Upload avatar")
	@PutMapping("/@me/avatar")
	public AttachmentsDTO uploadAvatar(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @RequestBody AttachmentsAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		return attachmentService.upload(authenticatedUser, attachment);
	}

	@Operation(summary = "Delete")
	@DeleteMapping("/@me")
	public OkDTO delete(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody UserDeleteDTO body) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();
		log.debug("USER deletion requested ({}) request", user.getId());

		userService.requestDelete(user, password);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm delete")
	@PostMapping("/@me/delete-confirm")
	public OkDTO deleteConfirm(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody OTPDTO body) throws OTPExpiredException, OTPsInvalidException {
		log.debug("USER deletion confirm ({}) request", user.getId());

		userService.confirmDelete(user, body.getOTP());

		return new OkDTO(true);
	}
}
