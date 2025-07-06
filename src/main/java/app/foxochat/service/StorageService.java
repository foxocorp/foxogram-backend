package app.foxochat.service;

import app.foxochat.dto.internal.MediaPresignedURLDTO;
import app.foxochat.exception.media.UploadFailedException;

public interface StorageService {

    MediaPresignedURLDTO getPresignedUrl(String bucketName) throws UploadFailedException;
}
