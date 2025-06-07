package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Schema(name = "Info")
data class InfoDTO(
    val version: String?, val cdnURL: String?, val gatewayURL: String?, val appURL: String?
)
