package su.foxogram.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.request.ChannelCreateDTO;
import su.foxogram.dtos.api.request.ChannelEditDTO;
import su.foxogram.dtos.api.request.MessageCreateDTO;
import su.foxogram.dtos.api.response.*;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.channel.ChannelAlreadyExistException;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.exceptions.member.MemberAlreadyInChannelException;
import su.foxogram.exceptions.member.MemberInChannelNotFoundException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.exceptions.message.AttachmentsCannotBeEmpty;
import su.foxogram.exceptions.message.MessageCannotBeEmpty;
import su.foxogram.exceptions.message.MessageNotFoundException;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.services.ChannelsService;
import su.foxogram.services.MessagesService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "Channels")
@RequestMapping(value = APIConstants.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;

	private final MessagesService messagesService;

	public ChannelsController(ChannelsService channelsService, MessagesService messagesService) {
		this.channelsService = channelsService;
		this.messagesService = messagesService;
	}

	@Operation(summary = "Create channel")
	@PostMapping("/")
	public ChannelDTO createChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @Valid @RequestBody ChannelCreateDTO body) throws ChannelAlreadyExistException, ChannelNotFoundException {
		Channel channel = channelsService.createChannel(user, body);

		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Get channel by id")
	@GetMapping("/{channelId}")
	public ChannelDTO getChannelById(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) {
		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Get channel by name")
	@GetMapping("/@{name}")
	public ChannelDTO getChannelByName(@PathVariable String name) throws ChannelNotFoundException {
		return new ChannelDTO(channelsService.getChannelByName(name), null);
	}

	@Operation(summary = "Edit channel")
	@PatchMapping("/{channelId}")
	public ChannelDTO editChannel(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @Valid @ModelAttribute ChannelEditDTO body, @PathVariable String channelId) throws ChannelAlreadyExistException, JsonProcessingException, MissingPermissionsException, UploadFailedException {
		channel = channelsService.editChannel(member, channel, body);

		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Upload avatar")
	@PutMapping("/{channelId}/icon")
	public AttachmentsDTO uploadAvatar(@RequestBody AttachmentsAddDTO attachment, @PathVariable String channelId) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		return channelsService.uploadIcon(attachment);
	}

	@Operation(summary = "Delete channel")
	@DeleteMapping("/{channelId}")
	public OkDTO deleteChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) throws MissingPermissionsException, JsonProcessingException {
		channelsService.deleteChannel(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Join channel")
	@PutMapping("/{channelId}/members/@me")
	public MemberDTO joinChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) throws MemberAlreadyInChannelException, JsonProcessingException, ChannelNotFoundException {
		Member member = channelsService.joinUser(channel, user);

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Leave channel")
	@DeleteMapping("/{channelId}/members/@me")
	public OkDTO leaveChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) throws MemberInChannelNotFoundException, JsonProcessingException {
		channelsService.leaveUser(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Get member")
	@GetMapping("/{channelId}/members/{memberId}")
	public MemberDTO getMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable String memberId) throws MemberInChannelNotFoundException {
		if (Objects.equals(memberId, "@me")) {
			memberId = String.valueOf(user.getId());
		}

		Member member = channelsService.getMember(channel, Long.parseLong(memberId));

		if (member == null) throw new MemberInChannelNotFoundException();

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Get members")
	@GetMapping("/{channelId}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) {
		return channelsService.getMembers(channel);
	}

	@Operation(summary = "Get messages")
	@GetMapping("/{channelId}/messages")
	public List<MessageDTO> getMessages(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @RequestParam(defaultValue = "0") long before, @RequestParam(defaultValue = "25") int limit) {
		if (before <= 0) {
			before = System.currentTimeMillis();
		}

		if (limit <= 0) {
			limit = 25;
		}

		return messagesService.getMessages(before, limit, channel);
	}

	@Operation(summary = "Get message")
	@GetMapping("/{channelId}/messages/{messageId}")
	public MessageDTO getMessage(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId) throws MessageNotFoundException {
		return messagesService.getMessage(messageId, channel);
	}

	@Operation(summary = "Create message")
	@PostMapping("/{channelId}/messages")
	public MessageDTO createMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @RequestBody @Valid MessageCreateDTO body) throws JsonProcessingException, MessageCannotBeEmpty, MissingPermissionsException, UnknownAttachmentsException, ChannelNotFoundException {
		if (body.getContent().isBlank()) {
			throw new MessageCannotBeEmpty();
		}

		Message message = messagesService.addMessage(channel, user, body);

		return new MessageDTO(message, null, true);
	}

	@Operation(summary = "Add attachments")
	@PutMapping("/{channelId}/attachments")
	public List<AttachmentsDTO> addAttachments(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @RequestBody List<AttachmentsAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty {
		if (attachments == null || attachments.isEmpty()) {
			throw new AttachmentsCannotBeEmpty();
		}

		return messagesService.addAttachments(channel, user, attachments);
	}

	@Operation(summary = "Delete message")
	@DeleteMapping("/{channelId}/messages/{messageId}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		messagesService.deleteMessage(messageId, member, channel);

		return new OkDTO(true);
	}

	@Operation(summary = "Edit message")
	@PatchMapping("/{channelId}/messages/{messageId}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId, @Valid @RequestBody MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		List<Message> message = List.of(messagesService.editMessage(messageId, channel, member, body));

		return new MessagesDTO(message);
	}
}
