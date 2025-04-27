package su.foxogram.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import su.foxogram.interceptors.AuthenticationInterceptor;
import su.foxogram.interceptors.ChannelInterceptor;
import su.foxogram.interceptors.MemberInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final AuthenticationInterceptor authenticationInterceptor;

	private final ChannelInterceptor channelInterceptor;

	private final MemberInterceptor memberInterceptor;

	@Autowired
	public WebConfig(AuthenticationInterceptor authenticationInterceptor, ChannelInterceptor channelInterceptor, MemberInterceptor memberInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
		this.channelInterceptor = channelInterceptor;
		this.memberInterceptor = memberInterceptor;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("*")
				.allowedHeaders("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor).excludePathPatterns("/info", "/auth/register", "/auth/login", "/auth/reset-password", "/auth/reset-password/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health");
		registry.addInterceptor(channelInterceptor).excludePathPatterns("/info", "/auth/**", "/users/**", "/channels/", "/channels/@**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health");
		registry.addInterceptor(memberInterceptor).excludePathPatterns("/info", "/auth/**", "/users/**", "/channels/", "/channels/@**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health");
	}
}
