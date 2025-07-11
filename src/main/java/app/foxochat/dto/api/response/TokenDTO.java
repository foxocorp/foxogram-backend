package app.foxochat.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Schema(name = "Token")
public class TokenDTO {

    private Mono<String> accessToken;

    public TokenDTO(Mono<String> accessToken) {
        this.accessToken = accessToken.flatMap(Mono::just);
    }
}
