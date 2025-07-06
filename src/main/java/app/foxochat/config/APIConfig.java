package app.foxochat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("api")
@Getter
@Setter
public class APIConfig {

    private String version;

    private String env;

    private String url;

    @Value("${api.cdn.url}")
    private String cdnURL;

    @Value("${api.gateway.production_url}")
    private String gatewayURL;

    @Value("${api.gateway.development_url}")
    private String devGatewayURL;

    @Value("${api.app.production_url}")
    private String appURL;

    @Value("${api.app.development_url}")
    private String devAppURL;

    @Bean
    public boolean isDevelopment() {
        return env.equals("dev");
    }
}
