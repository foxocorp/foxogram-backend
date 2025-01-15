package su.foxogram.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.api.request.ChannelCreateDTO;
import su.foxogram.dtos.api.request.ChannelEditDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.dtos.api.response.MemberDTO;
import su.foxogram.dtos.api.response.OkDTO;
import su.foxogram.exceptions.channel.ChannelAlreadyExistException;
import su.foxogram.exceptions.member.MemberAlreadyInChannelException;
import su.foxogram.exceptions.member.MemberInChannelNotFoundException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.services.ChannelsService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "Channels")
@RequestMapping(value = APIConstants.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;

	public ChannelsController(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@Operation(summary = "Create channel")
	@PostMapping("/")
	public ChannelDTO createChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @Valid @RequestBody ChannelCreateDTO body) throws ChannelAlreadyExistException {
		Channel channel = channelsService.createChannel(user, body);

		return new ChannelDTO(channel, false);
	}

	@Operation(summary = "Get channel")
	@GetMapping("/{name}")
	public ChannelDTO getChannel(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) {
		return new ChannelDTO(channel, false);
	}

	@Operation(summary = "Edit channel")
	@PatchMapping("/{name}")
	public ChannelDTO editChannel(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @ModelAttribute ChannelEditDTO body) throws MissingPermissionsException, ChannelAlreadyExistException, JsonProcessingException {
		channel = channelsService.editChannel(member, channel, body);

		return new ChannelDTO(channel, false);
	}

	@Operation(summary = "Delete channel")
	@DeleteMapping("/{name}")
	public OkDTO deleteChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MissingPermissionsException, JsonProcessingException {
		channelsService.deleteChannel(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Join channel")
	@PutMapping("/{name}/members/@me")
	public MemberDTO joinChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MemberAlreadyInChannelException, JsonProcessingException {
		Member member = channelsService.joinUser(channel, user);

		return new MemberDTO(member);
	}

	@Operation(summary = "Leave channel")
	@DeleteMapping("/{name}/members/@me")
	public OkDTO leaveChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MemberInChannelNotFoundException, JsonProcessingException {
		channelsService.leaveUser(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Get member")
	@GetMapping("/{name}/members/{memberUsername}")
	public MemberDTO getMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable String memberUsername) throws MemberInChannelNotFoundException {
		if (Objects.equals(memberUsername, "@me")) {
			memberUsername = user.getUsername();
		}

		Member member = channelsService.getMember(channel, memberUsername);

		if (member == null) throw new MemberInChannelNotFoundException();

		return new MemberDTO(member);
	}

	@Operation(summary = "Get members")
	@GetMapping("/{name}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) {
		return channelsService.getMembers(channel);
	}
}
