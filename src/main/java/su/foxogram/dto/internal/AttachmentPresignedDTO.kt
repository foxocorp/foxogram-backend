package su.foxogram.dto.internal

import lombok.Getter
import lombok.Setter
import su.foxogram.model.Attachment

@Getter
@Setter
data class AttachmentPresignedDTO(val url: String, val uuid: String, val attachment: Attachment)
