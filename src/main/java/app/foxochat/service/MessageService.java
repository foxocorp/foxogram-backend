package app.foxochat.service;

import app.foxochat.dto.api.request.AttachmentAddDTO;
import app.foxochat.dto.api.request.MessageCreateDTO;
import app.foxochat.dto.api.response.UploadAttachmentDTO;
import app.foxochat.exception.member.MemberInChannelNotFoundException;
import app.foxochat.exception.member.MissingPermissionsException;
import app.foxochat.exception.message.AttachmentsCannotBeEmpty;
import app.foxochat.exception.message.MessageNotFoundException;
import app.foxochat.model.Channel;
import app.foxochat.model.Member;
import app.foxochat.model.Message;
import app.foxochat.model.User;

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
