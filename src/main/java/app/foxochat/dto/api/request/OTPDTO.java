package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "OTP")
public class OTPDTO {

    @NotNull(message = "OTP" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = 6, max = 6, message = ValidationConstant.Messages.OTP_NAME_WRONG_LENGTH)
    private String OTP;
}
