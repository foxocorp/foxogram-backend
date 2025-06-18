package app.foxochat.service.impl;

import app.foxochat.dto.internal.AttachmentPresignedDTO;
import app.foxochat.service.StorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioAsyncClient;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

	private final MinioAsyncClient minioClient;

	public StorageServiceImpl(MinioAsyncClient minioClient) {
		this.minioClient = minioClient;
	}

	@Override
	public AttachmentPresignedDTO getPresignedUrl(String bucketName) {
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("response-content-type", "application/json");

		String url;
		String uuid = String.valueOf(UUID.randomUUID());

		try {
			url = minioClient.getPresignedObjectUrl(
					GetPresignedObjectUrlArgs.builder()
							.method(Method.PUT)
							.bucket(bucketName)
							.object(uuid)
							.expiry(10, TimeUnit.MINUTES)
							.extraQueryParams(reqParams)
							.build());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		log.debug("Successfully get presigned url to bucket {} with uuid {}", bucketName, uuid);
		return new AttachmentPresignedDTO(url, uuid, null);
	}
}
