package su.foxogram.service;

import su.foxogram.dto.api.request.AttachmentAddDTO;
import su.foxogram.dto.api.response.UploadAttachmentDTO;
import su.foxogram.dto.internal.AttachmentPresignedDTO;
import su.foxogram.exception.message.AttachmentsCannotBeEmpty;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.model.Attachment;
import su.foxogram.model.User;

import java.util.List;

public interface AttachmentService {

	AttachmentPresignedDTO getPresignedURLAndSave(AttachmentAddDTO attachment, User user);

	List<UploadAttachmentDTO> uploadAll(User user, List<AttachmentAddDTO> attachments);

	AttachmentPresignedDTO upload(User user, AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty;

	List<Attachment> get(User user, List<Long> attachmentsIds) throws UnknownAttachmentsException;

	Attachment getById(long id) throws UnknownAttachmentsException;
}
