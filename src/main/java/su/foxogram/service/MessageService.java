package su.foxogram.service;

import su.foxogram.dto.api.request.AttachmentAddDTO;
import su.foxogram.dto.api.request.MessageCreateDTO;
import su.foxogram.dto.api.response.UploadAttachmentDTO;
import su.foxogram.exception.member.MemberInChannelNotFoundException;
import su.foxogram.exception.member.MissingPermissionsException;
import su.foxogram.exception.message.AttachmentsCannotBeEmpty;
import su.foxogram.exception.message.MessageNotFoundException;
import su.foxogram.model.Channel;
import su.foxogram.model.Member;
import su.foxogram.model.Message;
import su.foxogram.model.User;

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
