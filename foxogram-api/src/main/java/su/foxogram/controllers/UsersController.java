package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.api.request.CodeDTO;
import su.foxogram.dtos.api.request.UserDeleteDTO;
import su.foxogram.dtos.api.request.UserEditDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.dtos.api.response.OkDTO;
import su.foxogram.dtos.api.response.UserDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.code.CodeExpiredException;
import su.foxogram.exceptions.code.CodeIsInvalidException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.exceptions.user.UserNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.services.UsersService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "Users")
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {
	private final UsersService usersService;

	private final MemberRepository memberRepository;

	public UsersController(UsersService usersService, MemberRepository memberRepository) {
		this.usersService = usersService;
		this.memberRepository = memberRepository;
	}

	@Operation(summary = "Get user")
	@GetMapping("/{username}")
	public UserDTO getUser(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @PathVariable String username) throws UserNotFoundException {
		if (Objects.equals(username, "@me")) {
			List<Long> channels = memberRepository.findAllByUserId(authenticatedUser.getId())
					.stream()
					.map(Member::getChannel)
					.map(Channel::getId)
					.collect(Collectors.toList());

			return new UserDTO(authenticatedUser, channels, true, true);
		}

		return new UserDTO(usersService.getUser(username), null, false, false);
	}

	@Operation(summary = "Get user channels")
	@GetMapping("/@me/channels")
	public List<ChannelDTO> getUserChannels(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser) {
		return usersService.getChannels(authenticatedUser);
	}

	@Operation(summary = "Edit user")
	@PatchMapping("/@me")
	public UserDTO editUser(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @Valid @ModelAttribute UserEditDTO body) throws UserCredentialsDuplicateException, UploadFailedException {
		authenticatedUser = usersService.editUser(authenticatedUser, body);

		return new UserDTO(authenticatedUser, null, true, true);
	}

	@Operation(summary = "Delete")
	@DeleteMapping("/@me")
	public OkDTO deleteUser(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody UserDeleteDTO body) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();
		log.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		usersService.requestUserDelete(user, password);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm delete")
	@PostMapping("/@me/delete-confirm")
	public OkDTO deleteUserConfirm(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody CodeDTO body) throws CodeExpiredException, CodeIsInvalidException {
		log.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		usersService.confirmUserDelete(user, body.getCode());

		return new OkDTO(true);
	}
}
