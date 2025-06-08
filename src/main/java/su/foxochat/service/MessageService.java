package su.foxochat.service;

import su.foxochat.dto.api.request.AttachmentAddDTO;
import su.foxochat.dto.api.request.MessageCreateDTO;
import su.foxochat.dto.api.response.UploadAttachmentDTO;
import su.foxochat.exception.member.MemberInChannelNotFoundException;
import su.foxochat.exception.member.MissingPermissionsException;
import su.foxochat.exception.message.AttachmentsCannotBeEmpty;
import su.foxochat.exception.message.MessageNotFoundException;
import su.foxochat.model.Channel;
import su.foxochat.model.Member;
import su.foxochat.model.Message;
import su.foxochat.model.User;

import java.util.List;

public interface MessageService {

	List<Message> getAllByChannel(long before, int limit, Channel channel);

	Message getByIdAndChannel(long id, Channel channel) throws MessageNotFoundException;

	Message add(Channel channel, User user, MessageCreateDTO body) throws Exception;

	List<UploadAttachmentDTO> addAttachments(Channel channel, User user, List<AttachmentAddDTO> attachments) throws MissingPermissionsException, AttachmentsCannotBeEmpty, MemberInChannelNotFoundException;

	void delete(long id, Member member, Channel channel) throws Exception;

	Message update(long id, Channel channel, Member member, MessageCreateDTO body) throws Exception;

	Message getLastByChannel(Channel channel);
}
