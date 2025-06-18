package app.foxochat.service;

import app.foxochat.dto.internal.AttachmentPresignedDTO;

public interface StorageService {

	AttachmentPresignedDTO getPresignedUrl(String bucketName);
}
