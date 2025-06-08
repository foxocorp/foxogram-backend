package su.foxochat.service;

import su.foxochat.dto.internal.AttachmentPresignedDTO;

public interface StorageService {

	AttachmentPresignedDTO getPresignedUrl(String bucketName);
}
