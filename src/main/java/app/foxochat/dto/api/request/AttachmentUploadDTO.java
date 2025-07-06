package app.foxochat.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Schema(name = "AttachmentsAdd")
public class AttachmentUploadDTO {

    private String filename;

    private String contentType;

    private boolean spoiler;
}
