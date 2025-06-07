package su.foxogram.dto.gateway

import lombok.Getter
import lombok.Setter

@Getter
@Setter
class StatusDTO(
    var userId: Long = 0,

    var status: Int = 0
)
