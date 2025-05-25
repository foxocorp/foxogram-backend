package su.foxogram.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioAsyncClient;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.dto.internal.AttachmentPresignedDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StorageService {

	private final MinioAsyncClient minioClient;

	@Autowired
	public StorageService(MinioAsyncClient minioClient) {
		this.minioClient = minioClient;
	}

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
