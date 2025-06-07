package su.foxogram.dto.gateway

import lombok.Getter
import lombok.Setter

@Getter
@Setter
class EventDTO(
    var op: Int = 0,

    var d: Any? = null,

    var s: Int = 0,

    var t: String? = null
)
