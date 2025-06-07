package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Schema(name = "Token")
data class TokenDTO(val accessToken: String?)
