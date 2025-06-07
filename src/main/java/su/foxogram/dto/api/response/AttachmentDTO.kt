package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter
import su.foxogram.model.Attachment

@Getter
@Setter
@Schema(name = "Attachment")
data class AttachmentDTO(val attachment: Attachment) {
    var id: Long = attachment.id

    var uuid: String? = attachment.uuid

    var filename: String? = attachment.filename

    var contentType: String? = attachment.contentType

    var flags: Long = attachment.flags
}
