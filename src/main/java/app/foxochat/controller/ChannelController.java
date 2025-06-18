package app.foxochat.controller;

import app.foxochat.constant.APIConstant;
import app.foxochat.constant.AttributeConstant;
import app.foxochat.dto.api.request.AttachmentAddDTO;
import app.foxochat.dto.api.request.ChannelCreateDTO;
import app.foxochat.dto.api.request.ChannelEditDTO;
import app.foxochat.dto.api.request.MessageCreateDTO;
import app.foxochat.dto.api.response.*;
import app.foxochat.dto.internal.AttachmentPresignedDTO;
import app.foxochat.exception.channel.ChannelAlreadyExistException;
import app.foxochat.exception.channel.ChannelNotFoundException;
import app.foxochat.exception.member.MemberInChannelNotFoundException;
import app.foxochat.exception.member.MissingPermissionsException;
import app.foxochat.exception.message.AttachmentsCannotBeEmpty;
import app.foxochat.exception.message.MessageCannotBeEmpty;
import app.foxochat.exception.message.MessageNotFoundException;
import app.foxochat.exception.message.UnknownAttachmentsException;
import app.foxochat.model.Channel;
import app.foxochat.model.Member;
import app.foxochat.model.Message;
import app.foxochat.model.User;
import app.foxochat.service.AttachmentService;
import app.foxochat.service.ChannelService;
import app.foxochat.service.MemberService;
import app.foxochat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "Channels")
@RequestMapping(value = APIConstant.CHANNELS, produces = "application/json")
public class ChannelController {

	private final ChannelService channelService;

	private final MessageService messageService;

	private final MemberService memberService;

	private final AttachmentService attachmentService;

	public ChannelController(ChannelService channelService, MessageService messageService, MemberService memberService, AttachmentService attachmentService) {
		this.channelService = channelService;
		this.messageService = messageService;
		this.memberService = memberService;
		this.attachmentService = attachmentService;
	}

	@Operation(summary = "Create channel")
	@PostMapping("/")
	public ChannelDTO create(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestBody ChannelCreateDTO body) throws ChannelAlreadyExistException {
		Channel channel = channelService.add(user, body);

		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Get channel by id")
	@GetMapping("/{channelId}")
	public ChannelDTO getById(@RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId) {
		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Get channel by name")
	@GetMapping("/@{name}")
	public ChannelDTO getByName(@PathVariable String name) throws ChannelNotFoundException {
		return new ChannelDTO(channelService.getByName(name), null);
	}

	@Operation(summary = "Edit channel")
	@PatchMapping("/{channelId}")
	public ChannelDTO edit(@RequestAttribute(value = AttributeConstant.MEMBER) Member member, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @RequestBody ChannelEditDTO body) throws Exception {
		channel = channelService.update(member, channel, body);

		return new ChannelDTO(channel, null);
	}

	@Operation(summary = "Upload icon")
	@PutMapping("/{channelId}/icon")
	public UploadAttachmentDTO uploadAvatar(@PathVariable String channelId, @RequestBody AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		AttachmentPresignedDTO data = attachmentService.upload(null, attachment);

		return new UploadAttachmentDTO(data.getUrl(), data.getAttachment().getId());
	}

	@Operation(summary = "Delete channel")
	@DeleteMapping("/{channelId}")
	public OkDTO delete(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId) throws Exception {
		channelService.delete(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Join channel")
	@PutMapping("/{channelId}/members/@me")
	public MemberDTO addMember(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId) throws Exception {
		Member member = channelService.addMember(channel, user);

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Leave channel")
	@DeleteMapping("/{channelId}/members/@me")
	public OkDTO removeMember(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId) throws Exception {
		channelService.removeMember(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Get member")
	@GetMapping("/{channelId}/members/{memberId}")
	public MemberDTO getMember(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable String memberId) throws MemberInChannelNotFoundException {
		if (Objects.equals(memberId, "@me")) {
			memberId = String.valueOf(user.getId());
		}

		Member member = memberService.getByChannelIdAndUserId(channel.getId(), Long.parseLong(memberId))
				.orElseThrow(MemberInChannelNotFoundException::new);

		return new MemberDTO(member, true);
	}

	@Operation(summary = "Get members")
	@GetMapping("/{channelId}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId) {
		return memberService.getAllByChannelId(channel.getId()).stream()
				.map(member -> new MemberDTO(member, false))
				.toList();
	}

	@Operation(summary = "Get messages")
	@GetMapping("/{channelId}/messages")
	public List<MessageDTO> getMessages(@RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @RequestParam(defaultValue = "0") long before, @RequestParam(defaultValue = "25") int limit) {
		if (before <= 0) {
			before = System.currentTimeMillis();
		}

		if (limit <= 0) {
			limit = 25;
		}

		return messageService.getAllByChannel(before, limit, channel).stream()
				.map(message -> new MessageDTO(message, false))
				.collect(Collectors.toList());
	}

	@Operation(summary = "Get message")
	@GetMapping("/{channelId}/messages/{messageId}")
	public MessageDTO getMessage(@RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId) throws MessageNotFoundException {
		return new MessageDTO(messageService.getByIdAndChannel(messageId, channel), true);
	}

	@Operation(summary = "Create message")
	@PostMapping("/{channelId}/messages")
	public MessageDTO createMessage(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @RequestBody MessageCreateDTO body) throws Exception {
		if (body.getContent() == null && body.getAttachments() == null) {
			throw new MessageCannotBeEmpty();
		}

		Message message = messageService.add(channel, user, body);

		return new MessageDTO(message, true);
	}

	@Operation(summary = "Add attachments")
	@PutMapping("/{channelId}/attachments")
	public List<UploadAttachmentDTO> addAttachments(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable String channelId, @RequestBody List<AttachmentAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty, MemberInChannelNotFoundException {
		if (attachments == null || attachments.isEmpty()) {
			throw new AttachmentsCannotBeEmpty();
		}

		return messageService.addAttachments(channel, user, attachments);
	}

	@Operation(summary = "Delete message")
	@DeleteMapping("/{channelId}/messages/{messageId}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributeConstant.MEMBER) Member member, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId) throws Exception {
		messageService.delete(messageId, member, channel);

		return new OkDTO(true);
	}

	@Operation(summary = "Edit message")
	@PatchMapping("/{channelId}/messages/{messageId}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributeConstant.MEMBER) Member member, @RequestAttribute(value = AttributeConstant.CHANNEL) Channel channel, @PathVariable long channelId, @PathVariable long messageId, @RequestBody MessageCreateDTO body) throws Exception {
		List<Message> message = List.of(messageService.update(messageId, channel, member, body));

		return new MessagesDTO(message);
	}
}
