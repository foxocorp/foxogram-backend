package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.constants.StorageConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.response.AttachmentsDTO;
import su.foxogram.dtos.internal.AttachmentPresignedDTO;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.models.Attachment;
import su.foxogram.models.User;
import su.foxogram.repositories.AttachmentRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AttachmentService {

	public final AttachmentRepository attachmentRepository;

	public final StorageService storageService;

	public AttachmentService(AttachmentRepository attachmentRepository, StorageService storageService) {
		this.attachmentRepository = attachmentRepository;
		this.storageService = storageService;
	}

	public AttachmentPresignedDTO getPresignedURLAndSave(AttachmentsAddDTO attachment, User user) {
		AttachmentPresignedDTO dto = storageService.getPresignedUrl(StorageConstants.ATTACHMENTS_BUCKET);
		Attachment attachmentObj = attachmentRepository.save(new Attachment(0, user, dto.getUuid(), attachment.getFilename(), attachment.getContentType(), 0, true));

		return new AttachmentPresignedDTO(dto.getUrl(), dto.getUuid(), attachmentObj);
	}

	public List<AttachmentsDTO> uploadAttachments(User user, List<AttachmentsAddDTO> attachments) {
		List<AttachmentsDTO> attachmentsData = new ArrayList<>();

		attachments.forEach(attachment -> {
			AttachmentPresignedDTO dto = getPresignedURLAndSave(attachment, user);
			attachmentsData.add(new AttachmentsDTO(dto.getUrl(), dto.getAttachment().getId()));
		});

		return attachmentsData;
	}

	public AttachmentsDTO uploadAttachment(User user, AttachmentsAddDTO attachment) throws UnknownAttachmentsException {
		AttachmentPresignedDTO dto = getPresignedURLAndSave(attachment, user);

		if (user != null && dto.getAttachment().getUser().getId() != user.getId()) {
			throw new UnknownAttachmentsException();
		}

		return new AttachmentsDTO(dto.getUrl(), dto.getAttachment().getId());
	}

	public List<Attachment> getAttachments(User user, List<Long> attachmentsIds) throws UnknownAttachmentsException {
		List<Attachment> attachments = new ArrayList<>();

		if (!attachmentsIds.isEmpty()) {
			for (Long id : attachmentsIds) {
				Attachment attachment = attachmentRepository.findById(id).orElseThrow(UnknownAttachmentsException::new);

				if (attachment.getUser().getId() != user.getId()) throw new UnknownAttachmentsException();

				attachments.add(attachment);
			}
		}

		return attachments;
	}
}
