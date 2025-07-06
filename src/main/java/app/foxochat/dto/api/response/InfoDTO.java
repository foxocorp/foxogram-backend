package app.foxochat.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Info")
public class InfoDTO {

    private String version;

    private String cdnURL;

    private String gatewayURL;

    private String appURL;

    public InfoDTO(String version, String cdnURL, String gatewayURL, String appURL) {
        this.version = version;
        this.cdnURL = cdnURL;
        this.gatewayURL = gatewayURL;
        this.appURL = appURL;
    }
}
