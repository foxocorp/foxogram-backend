package app.foxochat.service;

import app.foxochat.dto.internal.MediaPresignedURLDTO;

public interface StorageService {

	MediaPresignedURLDTO getPresignedUrl(String bucketName);
}
