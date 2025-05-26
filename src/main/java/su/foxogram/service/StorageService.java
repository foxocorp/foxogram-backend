package su.foxogram.service;

import su.foxogram.dto.internal.AttachmentPresignedDTO;

public interface StorageService {

	AttachmentPresignedDTO getPresignedUrl(String bucketName);
}
