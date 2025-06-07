package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Schema(name = "Ok")
data class OkDTO(val ok: Boolean)
