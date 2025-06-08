package su.foxochat.service;

import su.foxochat.dto.api.request.AttachmentAddDTO;
import su.foxochat.dto.api.response.UploadAttachmentDTO;
import su.foxochat.dto.internal.AttachmentPresignedDTO;
import su.foxochat.exception.message.AttachmentsCannotBeEmpty;
import su.foxochat.exception.message.UnknownAttachmentsException;
import su.foxochat.model.Attachment;
import su.foxochat.model.User;

import java.util.List;

public interface AttachmentService {

	AttachmentPresignedDTO getPresignedURLAndSave(AttachmentAddDTO attachment, User user);

	List<UploadAttachmentDTO> uploadAll(User user, List<AttachmentAddDTO> attachments);

	AttachmentPresignedDTO upload(User user, AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty;

	List<Attachment> get(User user, List<Long> attachmentsIds) throws UnknownAttachmentsException;

	Attachment getById(long id) throws UnknownAttachmentsException;
}
