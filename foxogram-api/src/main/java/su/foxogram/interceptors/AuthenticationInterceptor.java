package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.user.UserEmailNotVerifiedException;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
	private static final Set<String> EMAIL_VERIFICATION_IGNORE_PATHS = Set.of(
			"/auth/email/verify",
			"/users/@me",
			"/auth/email/resend"
	);

	final AuthenticationService authenticationService;

	final APIConfig apiConfig;

	@Autowired
	public AuthenticationInterceptor(AuthenticationService authenticationService, APIConfig apiConfig) {
		this.authenticationService = authenticationService;
		this.apiConfig = apiConfig;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) return true;

		String requestURI = request.getRequestURI();
		boolean ignoreEmailVerification = EMAIL_VERIFICATION_IGNORE_PATHS.stream().anyMatch(requestURI::contains);
		if (apiConfig.isDevelopment()) ignoreEmailVerification = true;

		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		User user = authenticationService.authUser(accessToken, ignoreEmailVerification, false);

		request.setAttribute(AttributesConstants.USER, user);
		request.setAttribute(AttributesConstants.ACCESS_TOKEN, accessToken);

		log.debug("Authenticated user ({}) successfully", user.getUsername());
		return true;
	}
}
