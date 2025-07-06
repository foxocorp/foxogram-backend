package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Schema(name = "AttachmentsUpload")
public class AttachmentUploadDTO {

    @NotNull(message = "Filename" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = 5, max = ValidationConstant.Lengths.FILENAME,
            message = "Filename" + ValidationConstant.Messages.WRONG_LENGTH)
    private String filename;

    @NotNull(message = "Content type" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = 5, max = ValidationConstant.Lengths.CONTENT_TYPE,
            message = "Content Type" + ValidationConstant.Messages.WRONG_LENGTH)
    private String contentType;

    @NotNull(message = "Tumbhash" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = 1, max = ValidationConstant.Lengths.FILENAME,
            message = "Tumbhash" + ValidationConstant.Messages.WRONG_LENGTH)
    private String tumbhash;

    @NotNull(message = "Is spoiler" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    private boolean spoiler;
}
