package su.foxogram.dto.api.response

import lombok.Getter
import lombok.Setter

@Getter
@Setter
data class ExceptionDTO(val ok: Boolean, val code: Int, val message: String?)
