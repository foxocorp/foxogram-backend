package su.foxogram.dto.gateway.response

import lombok.Getter
import lombok.Setter

@Getter
@Setter
data class ExceptionDTO(val op: Int, val m: String?)
