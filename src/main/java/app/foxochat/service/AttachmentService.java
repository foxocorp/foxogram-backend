package app.foxochat.service;

import app.foxochat.dto.api.request.AttachmentAddDTO;
import app.foxochat.dto.api.response.UploadAttachmentDTO;
import app.foxochat.dto.internal.AttachmentPresignedDTO;
import app.foxochat.exception.message.AttachmentsCannotBeEmpty;
import app.foxochat.exception.message.UnknownAttachmentsException;
import app.foxochat.model.Attachment;
import app.foxochat.model.User;

import java.util.List;

public interface AttachmentService {

	AttachmentPresignedDTO getPresignedURLAndSave(AttachmentAddDTO attachment, User user);

	List<UploadAttachmentDTO> uploadAll(User user, List<AttachmentAddDTO> attachments);

	AttachmentPresignedDTO upload(User user, AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty;

	List<Attachment> get(User user, List<Long> attachmentsIds) throws UnknownAttachmentsException;

	Attachment getById(long id) throws UnknownAttachmentsException;
}
