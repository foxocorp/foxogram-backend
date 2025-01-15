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
import su.foxogram.exceptions.channel.ChannelNotFoundException;
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
	@GetMapping("/{idOrName}")
	public ChannelDTO getChannel(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) {
		return new ChannelDTO(channel, false);
	}

	@Operation(summary = "Edit channel")
	@PatchMapping("/{idOrName}")
	public ChannelDTO editChannel(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @ModelAttribute ChannelEditDTO body) throws MissingPermissionsException, ChannelAlreadyExistException, JsonProcessingException {
		channel = channelsService.editChannel(member, channel, body);

		return new ChannelDTO(channel, false);
	}

	@Operation(summary = "Delete channel")
	@DeleteMapping("/{idOrName}")
	public OkDTO deleteChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MissingPermissionsException, JsonProcessingException {
		channelsService.deleteChannel(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Join channel")
	@PutMapping("/{idOrName}/members/@me")
	public MemberDTO joinChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MemberAlreadyInChannelException, JsonProcessingException, ChannelNotFoundException {
		Member member = channelsService.joinUser(channel, user);

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Leave channel")
	@DeleteMapping("/{idOrName}/members/@me")
	public OkDTO leaveChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MemberInChannelNotFoundException, JsonProcessingException {
		channelsService.leaveUser(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Get member")
	@GetMapping("/{idOrName}/members/{memberId}")
	public MemberDTO getMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable String memberId) throws MemberInChannelNotFoundException {
		if (Objects.equals(memberId, "@me")) {
			memberId = String.valueOf(user.getId());
		}

		Member member = channelsService.getMember(channel, Long.parseLong(memberId));

		if (member == null) throw new MemberInChannelNotFoundException();

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Get members")
	@GetMapping("/{idOrName}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) {
		return channelsService.getMembers(channel);
	}
}
