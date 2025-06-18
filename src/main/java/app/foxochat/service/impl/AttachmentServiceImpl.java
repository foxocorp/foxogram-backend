package app.foxochat.service.impl;

import app.foxochat.constant.StorageConstant;
import app.foxochat.dto.api.request.AttachmentAddDTO;
import app.foxochat.dto.api.response.UploadAttachmentDTO;
import app.foxochat.dto.internal.AttachmentPresignedDTO;
import app.foxochat.exception.message.AttachmentsCannotBeEmpty;
import app.foxochat.exception.message.UnknownAttachmentsException;
import app.foxochat.model.Attachment;
import app.foxochat.model.User;
import app.foxochat.repository.AttachmentRepository;
import app.foxochat.service.AttachmentService;
import app.foxochat.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {

	public final AttachmentRepository attachmentRepository;

	public final StorageService storageService;

	public AttachmentServiceImpl(AttachmentRepository attachmentRepository, StorageService storageService) {
		this.attachmentRepository = attachmentRepository;
		this.storageService = storageService;
	}

	@Override
	public AttachmentPresignedDTO getPresignedURLAndSave(AttachmentAddDTO attachment, User user) {
		AttachmentPresignedDTO dto = storageService.getPresignedUrl(StorageConstant.ATTACHMENTS_BUCKET);
		Attachment attachmentObj = attachmentRepository.save(new Attachment(user, dto.getUuid(), attachment.getFilename(), attachment.getContentType(), 0, true));

		log.debug("Successfully got presigned url and saved attachment {}", dto.getUuid());
		return new AttachmentPresignedDTO(dto.getUrl(), dto.getUuid(), attachmentObj);
	}

	@Override
	public List<UploadAttachmentDTO> uploadAll(User user, List<AttachmentAddDTO> attachments) {
		List<UploadAttachmentDTO> attachmentsData = new ArrayList<>();

		attachments.forEach(attachment -> {
			AttachmentPresignedDTO dto = getPresignedURLAndSave(attachment, user);
			attachmentsData.add(new UploadAttachmentDTO(dto.getUrl(), dto.getAttachment().getId()));
		});

		log.debug("Successfully uploaded all attachments by user {}", user.getUsername());
		return attachmentsData;
	}

	@Override
	public AttachmentPresignedDTO upload(User user, AttachmentAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		if (attachment == null) throw new AttachmentsCannotBeEmpty();

		AttachmentPresignedDTO dto = getPresignedURLAndSave(attachment, user);

		if (user != null && dto.getAttachment().getUser().getId() != user.getId()) {
			throw new UnknownAttachmentsException();
		}

		log.debug("Successfully uploaded attachment by user {}", user.getUsername());
		return dto;
	}

	@Override
	public List<Attachment> get(User user, List<Long> attachmentsIds) throws UnknownAttachmentsException {
		List<Attachment> attachments = new ArrayList<>();

		if (!attachmentsIds.isEmpty()) {
			for (Long id : attachmentsIds) {
				Attachment attachment = attachmentRepository.findById(id).orElseThrow(UnknownAttachmentsException::new);

				if (attachment.getUser().getId() != user.getId()) throw new UnknownAttachmentsException();

				attachments.add(attachment);
			}
		}

		log.debug("Successfully got all attachments by user {}", user.getUsername());
		return attachments;
	}

	@Override
	public Attachment getById(long id) throws UnknownAttachmentsException {
		return attachmentRepository.findById(id).orElseThrow(UnknownAttachmentsException::new);
	}
}
