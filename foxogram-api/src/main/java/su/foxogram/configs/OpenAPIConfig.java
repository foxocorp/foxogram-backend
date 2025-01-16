package su.foxogram.configs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenAPIConfig {
	private final APIConfig apiConfig;

	public OpenAPIConfig(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
				.title("Foxogram")
				.version(apiConfig.getVersion());

		ExternalDocumentation externalDocs = new ExternalDocumentation()
				.description("External documentation")
				.url("https://github.com/foxocorp/foxogram-docs");

		List<Server> servers = Arrays.asList(
				new Server().url("https://api.dev.foxogram.su").description("Development"),
				new Server().url("https://api.foxogram.su").description("Production")
		);

		// Enable bearer authorization
		SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

		Components components = new Components()
				.addSecuritySchemes("Authorization", new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"));

		// Disable constructor fields and set snake_case
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
						.withCreatorVisibility(JsonAutoDetect.Visibility.NONE))
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

		ModelConverters.getInstance().addConverter(new ModelResolver(objectMapper));

		return new OpenAPI()
				.info(info)
				.externalDocs(externalDocs)
				.servers(servers)
				.addSecurityItem(securityRequirement)
				.components(components);
	}
}
