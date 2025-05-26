package su.foxogram.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import su.foxogram.config.APIConfig;
import su.foxogram.config.EmailConfig;
import su.foxogram.constant.EmailConstant;
import su.foxogram.service.OTPService;
import su.foxogram.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailServiceImpl implements su.foxogram.service.EmailService {

	private final OTPService otpService;

	private final ResourceLoader resourceLoader;

	private final JavaMailSender javaMailSender;

	private final EmailConfig emailConfig;

	private final APIConfig apiConfig;

	public EmailServiceImpl(OTPService otpService, JavaMailSender javaMailSender, ResourceLoader resourceLoader, EmailConfig emailConfig, APIConfig apiConfig) {
		this.otpService = otpService;
		this.javaMailSender = javaMailSender;
		this.resourceLoader = resourceLoader;
		this.emailConfig = emailConfig;
		this.apiConfig = apiConfig;
	}

	@Async
	@Override
	public void send(String to, long id, String type, String username, String digitCode, long issuedAt, long expiresAt, String token) {
		if (apiConfig.isDevelopment()) return;

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

		try {
			helper.setTo(to);
			helper.setFrom(emailConfig.getEmail());

			String subject = getSubjectByType(type);
			String htmlContent = getContentByType(username, digitCode, token);

			helper.setSubject(subject);
			helper.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
			log.debug("Email {} sent to {} successfully", type, to);

			otpService.save(id, type, digitCode, issuedAt, expiresAt);
		} catch (IllegalArgumentException | MessagingException | IOException e) {
			log.error("Error occurred while sending email to {}: {}", to, e.getMessage(), e);
		}
	}

	private String getSubjectByType(String type) {
		type = type.toUpperCase();

		return switch (EmailConstant.Type.valueOf(type)) {
			case ACCOUNT_DELETE -> "Confirm Your Account Deletion";
			case EMAIL_VERIFY -> "Confirm Your Email Address";
			case RESET_PASSWORD -> "Confirm Password Change";
		};
	}

	private String getContentByType(String username, String digitCode, String token) throws IOException {
		return readHTML().replace("{0}", username).replace("{1}", digitCode);//.replace("{2}", token);
	}

	private String readHTML() throws IOException {
		String templateName = "email";
		Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".html");

		if (!resource.exists()) {
			log.error("Template not found: {}", templateName);
			throw new IOException("Template file not found: " + templateName);
		}

		try (InputStream inputStream = resource.getInputStream()) {
			return StringUtils.inputStreamToString(inputStream, StandardCharsets.UTF_8);
		}
	}
}
