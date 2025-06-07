package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Attachments")
data class UploadAttachmentDTO(var url: String?, var id: Long)
