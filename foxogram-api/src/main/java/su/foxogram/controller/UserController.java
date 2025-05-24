package su.foxogram.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constant.APIConstant;
import su.foxogram.constant.AttributeConstant;
import su.foxogram.dto.api.request.AttachmentAddDTO;
import su.foxogram.dto.api.request.OTPDTO;
import su.foxogram.dto.api.request.UserDeleteDTO;
import su.foxogram.dto.api.request.UserEditDTO;
import su.foxogram.dto.api.response.UploadAttachmentDTO;
import su.foxogram.dto.api.response.ChannelDTO;
import su.foxogram.dto.api.response.OkDTO;
import su.foxogram.dto.api.response.UserDTO;
import su.foxogram.exception.message.AttachmentsCannotBeEmpty;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.exception.user.UserCredentialsDuplicateException;
import su.foxogram.exception.user.UserCredentialsIsInvalidException;
import su.foxogram.exception.user.UserNotFoundException;
import su.foxogram.model.Channel;
import su.foxogram.model.Message;
import su.foxogram.model.User;
import su.foxogram.service.AttachmentService;
import su.foxogram.service.MemberService;
import su.foxogram.service.MessageService;
import su.foxogram.service.UserService;

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
	public UserDTO edit(@RequestAttribute(value = AttributeConstant.USER) User authenticatedUser, @Valid @ModelAttribute UserEditDTO body) throws UserCredentialsDuplicateException, UnknownAttachmentsException {
		authenticatedUser = userService.update(authenticatedUser, body);

		return new UserDTO(authenticatedUser, null, true, true);
	}

	@Operation(summary = "Upload avatar")
	@PutMapping("/@me/avatar")
	public UploadAttachmentDTO uploadAvatar(@RequestAttribute(value = AttributeConstant.USER) User authenticatedUser, @RequestBody AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		return attachmentService.upload(authenticatedUser, attachment);
	}

	@Operation(summary = "Delete")
	@DeleteMapping("/@me")
	public OkDTO delete(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestBody UserDeleteDTO body) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();
		log.debug("USER deletion requested ({}) request", user.getId());

		userService.requestDelete(user, password);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm delete")
	@PostMapping("/@me/delete-confirm")
	public OkDTO deleteConfirm(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestBody OTPDTO body) throws OTPExpiredException, OTPsInvalidException {
		log.debug("USER deletion confirm ({}) request", user.getId());

		userService.confirmDelete(user, body.getOTP());

		return new OkDTO(true);
	}
}
