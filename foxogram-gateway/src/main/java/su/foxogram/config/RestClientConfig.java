package su.foxogram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import su.foxogram.configs.APIConfig;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {
	private final APIConfig apiConfig;

	public RestClientConfig(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	@Bean
	public RestClient restClient() {
		String baseUrl = apiConfig.isDevelopment() ? apiConfig.getDevAppURL() : apiConfig.getAppURL();

		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(1000))
				.build();

		return RestClient.builder()
				.requestFactory(new JdkClientHttpRequestFactory(httpClient))
				.baseUrl(baseUrl)
				.build();
	}
}
