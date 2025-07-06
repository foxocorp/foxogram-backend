package app.foxochat.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Exception")
public class ExceptionDTO {

    private boolean ok;

    private int code;

    private String message;

    public ExceptionDTO(boolean ok, int code, String message) {
        this.ok = ok;
        this.code = code;
        this.message = message;
    }
}
