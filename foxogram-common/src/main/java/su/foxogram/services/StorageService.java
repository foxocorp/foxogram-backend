package su.foxogram.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.models.Channel;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.MetadataExtractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class StorageService {

	private static final String ALGORITHM = "MD5";

	private static final String CONTENT_TYPE_WEBP = "image/webp";

	private static final String CONTENT_TYPE_PNG = "image/png";

	private final MinioAsyncClient minioClient;

	private final UserRepository userRepository;

	private final ChannelRepository channelRepository;

	private final MessageRepository messageRepository;

	@Autowired
	public StorageService(MinioAsyncClient minioClient, UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
		this.minioClient = minioClient;
		this.userRepository = userRepository;
		this.channelRepository = channelRepository;
		this.messageRepository = messageRepository;
	}

	public String uploadToMinio(MultipartFile file, String bucketName) throws RuntimeException, IOException, ExecutionException, InterruptedException, NoSuchAlgorithmException {
		byte[] byteArray = file.getBytes();
		String fileName = file.getOriginalFilename();
		assert fileName != null;
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		String fileContentType = file.getContentType();
		assert fileContentType != null;
		String fileType = fileContentType.substring(0, fileContentType.indexOf("/"));
		String fileHash = getHash(byteArray);
		log.info("Uploading file ({}, {}, {}, {}) to bucket ({})", fileName, fileExtension, fileType, fileContentType, bucketName);

		if (isHashExists(fileHash)) {
			log.info("Duplicate file ({}) found. Skipping upload...", fileHash);
			return fileHash;
		}

		if (!isBucketExists(bucketName).get()) createBucket(bucketName);

		uploadFile(byteArray, fileHash, fileExtension, fileType, fileContentType, bucketName);

		return fileHash;
	}

	public String uploadIdentityImage(MultipartFile file, String bucketName) throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException {
		byte[] byteArray = file.getBytes();
		String fileName = file.getOriginalFilename();
		assert fileName != null;
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		String fileContentType = file.getContentType();
		assert fileContentType != null;
		String fileType = fileContentType.substring(0, fileContentType.indexOf("/"));
		String fileHash = getHash(byteArray);

		log.info("Uploading file ({}, {}, {}, {}) to bucket ({})", fileName, fileExtension, fileType, fileContentType, bucketName);

		if (isHashExists(fileHash)) {
			log.info("Duplicate file ({}) found. Skipping upload...", fileHash);
			return fileHash;
		}

		if (!isBucketExists(bucketName).get()) createBucket(bucketName);

		try (InputStream inputStream = new ByteArrayInputStream(byteArray)) {

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".png").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_PNG)
							.build());

			log.info("Image ({}) in PNG uploaded to bucket ({}) to CDN successfully", fileHash, bucketName);

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".webp").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_WEBP)
							.build());

			log.info("Image ({}) in WEBP uploaded to bucket ({}) to CDN successfully", fileHash, bucketName);

			return fileHash;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void uploadFile(byte[] byteArray, String fileHash, String fileExtension, String fileType, String fileContentType, String bucketName) {
		try {
			InputStream inputStream = new ByteArrayInputStream(byteArray);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			if (fileType.equals("image")) {
				MetadataExtractor.removeMetadata(fileExtension, inputStream, outputStream);
				inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			}

			minioClient.putObject(
					PutObjectArgs.builder()
							.bucket(bucketName)
							.object(fileHash + fileExtension)
							.stream(inputStream, inputStream.available(), -1)
							.contentType(fileContentType)
							.build());

			log.info("File ({}, {}, {}) uploaded to bucket ({}) to CDN successfully", fileHash, fileExtension, fileContentType, bucketName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isHashExists(String hash) {
		User user = userRepository.findByAvatar(hash);
		Channel channel = channelRepository.findByIcon(hash);
		List<String> attachments = getAllAttachments();

		return user != null || channel != null || attachments.contains(hash);
	}

	private void createBucket(String bucketName) {
		try {
			minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CompletableFuture<Boolean> isBucketExists(String bucketName) throws RuntimeException {
		try {
			return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getHash(byte[] imageBytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
		byte[] hashBytes = messageDigest.digest(imageBytes);
		StringBuilder hexString = new StringBuilder();

		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}

		return hexString.toString();
	}

	private List<String> getAllAttachments() {
		List<Message> messages = messageRepository.findAll();
		List<String> attachments = new ArrayList<>();
		for (Message message : messages) {
			if (message.getAttachments() != null) {
				attachments.addAll(message.getAttachments());
			}
		}

		return attachments;
	}
}