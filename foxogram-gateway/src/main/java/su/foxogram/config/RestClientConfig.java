package su.foxogram.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import su.foxogram.configs.APIConfig;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration
public class RestClientConfig {
	private final APIConfig apiConfig;

	private final ObjectMapper objectMapper;

	public RestClientConfig(APIConfig apiConfig, ObjectMapper objectMapper) {
		this.apiConfig = apiConfig;
		this.objectMapper = objectMapper;
	}

	@Bean
	public RestClient restClient() {
		String baseUrl = apiConfig.isDevelopment() ? apiConfig.getDevAppURL() : apiConfig.getAppURL();

		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(1000))
				.build();

		return RestClient.builder()
				.requestFactory(new JdkClientHttpRequestFactory(httpClient))
				.messageConverters(List.of(new MappingJackson2HttpMessageConverter(objectMapper)))
				.baseUrl(baseUrl)
				.build();
	}
}
