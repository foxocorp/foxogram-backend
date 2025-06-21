package app.foxochat.controller;

import app.foxochat.config.APIConfig;
import app.foxochat.constant.APIConstant;
import app.foxochat.dto.api.response.InfoDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Common")
@RequestMapping(value = APIConstant.COMMON, produces = "application/json")
public class CommonController {

	private final APIConfig apiConfig;

	public CommonController(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	@SecurityRequirements
	@Operation(summary = "Get info")
	@GetMapping("/info")
	public InfoDTO get() {
		String gatewayURL = apiConfig.isDevelopment() ? apiConfig.getDevGatewayURL() : apiConfig.getGatewayURL();
		String appURL = apiConfig.isDevelopment() ? apiConfig.getDevAppURL() : apiConfig.getAppURL();

		return new InfoDTO(apiConfig.getVersion(), apiConfig.getCdnURL(), gatewayURL, appURL);
	}

	@SuppressWarnings("SameReturnValue")
	@Hidden
	@SecurityRequirements
	@GetMapping("/actuator/health")
	public String health() {
		return "{\"status\":\"UP\"}";
	}
}
