package su.foxogram.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.exceptions.cdn.InvalidFileFormatException;
import su.foxogram.models.Attachment;
import su.foxogram.models.Channel;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.repositories.AttachmentRepository;
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

	private final AttachmentRepository attachmentRepository;

	@Autowired
	public StorageService(MinioAsyncClient minioClient, UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository, AttachmentRepository attachmentRepository) {
		this.minioClient = minioClient;
		this.userRepository = userRepository;
		this.channelRepository = channelRepository;
		this.messageRepository = messageRepository;
		this.attachmentRepository = attachmentRepository;
	}

	private static String getFileHash(byte[] imageBytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
		byte[] hashBytes = messageDigest.digest(imageBytes);
		StringBuilder hexString = new StringBuilder();

		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}

		return hexString.toString();
	}

	public Attachment uploadToMinio(MultipartFile file, String bucketName) throws RuntimeException, IOException, ExecutionException, InterruptedException, NoSuchAlgorithmException {
		byte[] byteArray = file.getBytes();
		FileData fileData = new FileData(file);

		log.info("Uploading file ({}, {}, {}, {}) to bucket ({})", fileData.getName(), fileData.getExtension(), fileData.getType(), fileData.getContentType(), bucketName);

		if (isHashExists(fileData.getHash())) {
			log.info("Duplicate file ({}) found. Skipping upload...", fileData.getHash());
			return new Attachment(fileData.getHash(), fileData.getName(), fileData.getContentType(), 0);
		}

		ensureBucketExists(bucketName);
		uploadFile(byteArray, fileData, bucketName);

		Attachment attachment = new Attachment(fileData.getHash(), fileData.getName(), fileData.getContentType(), 0);
		attachmentRepository.save(attachment);
		return attachment;
	}

	public String uploadIdentityImage(MultipartFile file, String bucketName) throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException, InvalidFileFormatException {
		byte[] byteArray = file.getBytes();
		FileData fileData = new FileData(file);

		log.info("Uploading file ({}, {}, {}, {}) to bucket ({})", fileData.getName(), fileData.getExtension(), fileData.getType(), fileData.getContentType(), bucketName);

		if (fileData.getType().equals("image")) throw new InvalidFileFormatException();

		if (isHashExists(fileData.getHash())) {
			log.info("Duplicate file ({}) found. Skipping upload...", fileData.getHash());
			return fileData.getHash();
		}

		ensureBucketExists(bucketName);

		try (InputStream inputStream = new ByteArrayInputStream(byteArray)) {
			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileData.getHash() + ".png").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_PNG)
							.build());

			log.info("Image ({}) in PNG uploaded to bucket ({}) to CDN successfully", fileData.getHash(), bucketName);

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileData.getHash() + ".webp").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_WEBP)
							.build());

			log.info("Image ({}) in WEBP uploaded to bucket ({}) to CDN successfully", fileData.getHash(), bucketName);

			return fileData.getHash();
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

	private void uploadFile(byte[] byteArray, FileData fileData, String bucketName) {
		try {
			InputStream inputStream = new ByteArrayInputStream(byteArray);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			if (fileData.getType().equals("image")) {
				MetadataExtractor.removeMetadata(fileData.getExtension(), inputStream, outputStream);
				inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			}

			minioClient.putObject(
					PutObjectArgs.builder()
							.bucket(bucketName)
							.object(fileData.getHash() + fileData.getExtension())
							.stream(inputStream, inputStream.available(), -1)
							.contentType(fileData.getContentType())
							.build());

			log.info("File ({}, {}, {}) uploaded to bucket ({}) to CDN successfully", fileData.getHash(), fileData.getExtension(), fileData.getContentType(), bucketName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	private void ensureBucketExists(String bucketName) throws ExecutionException, InterruptedException {
		if (!isBucketExists(bucketName).get()) createBucket(bucketName);
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

	@Getter
	public static class FileData {
		private final String name;

		private final byte[] byteArray;

		private final String extension;

		private final String type;

		private final String hash;

		private final String contentType;

		public FileData(MultipartFile file) throws IOException, NoSuchAlgorithmException {
			this.name = file.getOriginalFilename();
			this.byteArray = file.getBytes();
			assert name != null;
			this.extension = name.substring(name.lastIndexOf("."));
			this.hash = getFileHash(this.byteArray);
			this.contentType = file.getContentType();
			assert this.contentType != null;
			this.type = this.contentType.substring(0, this.contentType.indexOf("/"));
		}
	}
}