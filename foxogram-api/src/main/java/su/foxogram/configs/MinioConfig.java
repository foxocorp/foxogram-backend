package su.foxogram.configs;

import io.minio.MinioAsyncClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("minio")
@Setter
@Getter
public class MinioConfig {
	@Value("${minio.url}")
	private String url;

	@Value("${minio.name}")
	private String accessKey;

	@Value("${minio.secret}")
	private String accessSecret;

	@Bean
	public MinioAsyncClient minioClient() {
		return MinioAsyncClient.builder()
				.endpoint(url)
				.credentials(accessKey, accessSecret)
				.build();
	}
}
