package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.APIConstants;
import su.foxogram.dtos.api.response.InfoDTO;

@Slf4j
@RestController
@Tag(name = "Common")
@RequestMapping(value = APIConstants.COMMON, produces = "application/json")
public class CommonController {

	private final APIConfig apiConfig;

	@Autowired
	public CommonController(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	@Operation(summary = "Get info")
	@GetMapping("/info")
	public InfoDTO getInfo() {
		String gatewayURL = apiConfig.getGatewayURL();
		String appURL = apiConfig.getAppURL();

		if (apiConfig.isDevelopment()) gatewayURL = apiConfig.getDevGatewayURL();
		if (apiConfig.isDevelopment()) appURL = apiConfig.getDevAppURL();

		return new InfoDTO(apiConfig.getVersion(), apiConfig.getCdnURL(), gatewayURL, appURL);
	}

	@Hidden
	@GetMapping("/actuator/health")
	public String health() {
		return "{\"status\":\"UP\"}";
	}
}
