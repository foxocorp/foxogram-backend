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
import su.foxogram.services.AttachmentService;
import su.foxogram.services.ChannelService;
import su.foxogram.services.MemberService;
import su.foxogram.services.MessageService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "Channels")
@RequestMapping(value = APIConstants.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelService channelService;

	private final MessageService messageService;

	private final MemberService memberService;

	private final AttachmentService attachmentService;

	public ChannelsController(ChannelService channelService, MessageService messageService, MemberService memberService, AttachmentService attachmentService) {
		this.channelService = channelService;
		this.messageService = messageService;
		this.memberService = memberService;
		this.attachmentService = attachmentService;
	}

	@Operation(summary = "Create channel")
	@PostMapping("/")
	public ChannelDTO create(@RequestAttribute(value = AttributesConstants.USER) User user, @Valid @RequestBody ChannelCreateDTO body) throws ChannelAlreadyExistException, ChannelNotFoundException {
		Channel channel = channelService.add(user, body);

		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Get channel by id")
	@GetMapping("/{channelId}")
	public ChannelDTO getById(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) {
		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Get channel by name")
	@GetMapping("/@{name}")
	public ChannelDTO getByName(@PathVariable String name) throws ChannelNotFoundException {
		return new ChannelDTO(channelService.getByName(name), null);
	}

	@Operation(summary = "Edit channel")
	@PatchMapping("/{channelId}")
	public ChannelDTO edit(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id, @Valid @ModelAttribute ChannelEditDTO body, @PathVariable String channelId) throws ChannelAlreadyExistException, JsonProcessingException, MissingPermissionsException, UploadFailedException {
		channel = channelService.update(member, channel, body);

		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Upload avatar")
	@PutMapping("/{channelId}/icon")
	public AttachmentsDTO uploadAvatar(@RequestBody AttachmentsAddDTO attachment, @PathVariable String channelId) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		return attachmentService.upload(null, attachment);
	}

	@Operation(summary = "Delete channel")
	@DeleteMapping("/{channelId}")
	public OkDTO delete(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) throws MissingPermissionsException, JsonProcessingException, MemberInChannelNotFoundException {
		channelService.delete(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Join channel")
	@PutMapping("/{channelId}/members/@me")
	public MemberDTO addMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) throws MemberAlreadyInChannelException, JsonProcessingException {
		Member member = channelService.addMember(channel, user);

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Leave channel")
	@DeleteMapping("/{channelId}/members/@me")
	public OkDTO removeMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) throws MemberInChannelNotFoundException, JsonProcessingException {
		channelService.removeMember(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Get member")
	@GetMapping("/{channelId}/members/{memberId}")
	public MemberDTO getMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable String memberId) throws MemberInChannelNotFoundException {
		if (Objects.equals(memberId, "@me")) {
			memberId = String.valueOf(user.getId());
		}

		Member member = memberService.getByChannelAndUser(channel.getId(), Long.parseLong(memberId))
				.orElseThrow(MemberInChannelNotFoundException::new);

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Get members")
	@GetMapping("/{channelId}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId) {
		return memberService.getAllByChannelId(channel.getId()).stream()
				.map(member -> new MemberDTO(member, false))
				.toList();
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

		return messageService.getAllByChannel(before, limit, channel);
	}

	@Operation(summary = "Get message")
	@GetMapping("/{channelId}/messages/{messageId}")
	public MessageDTO getMessage(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId) throws MessageNotFoundException {
		return messageService.getByIdAndChannel(messageId, channel);
	}

	@Operation(summary = "Create message")
	@PostMapping("/{channelId}/messages")
	public MessageDTO createMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @RequestBody @Valid MessageCreateDTO body) throws JsonProcessingException, MessageCannotBeEmpty, MissingPermissionsException, UnknownAttachmentsException, ChannelNotFoundException, MemberInChannelNotFoundException {
		if (body.getContent() == null && body.getAttachments() == null) {
			throw new MessageCannotBeEmpty();
		}

		Message message = messageService.add(channel, user, body);

		return new MessageDTO(message, true);
	}

	@Operation(summary = "Add attachments")
	@PutMapping("/{channelId}/attachments")
	public List<AttachmentsDTO> addAttachments(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @RequestBody List<AttachmentsAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty, MemberInChannelNotFoundException {
		if (attachments == null || attachments.isEmpty()) {
			throw new AttachmentsCannotBeEmpty();
		}

		return messageService.addAttachments(channel, user, attachments);
	}

	@Operation(summary = "Delete message")
	@DeleteMapping("/{channelId}/messages/{messageId}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		messageService.delete(messageId, member, channel);

		return new OkDTO(true);
	}

	@Operation(summary = "Edit message")
	@PatchMapping("/{channelId}/messages/{messageId}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId, @Valid @RequestBody MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException, JsonProcessingException, ChannelNotFoundException {
		List<Message> message = List.of(messageService.update(messageId, channel, member, body));

		return new MessagesDTO(message);
	}
}
