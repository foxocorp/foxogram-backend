package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.user.UserEmailNotVerifiedException;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

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

	@Autowired
	public AuthenticationInterceptor(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		String requestURI = request.getRequestURI();
		boolean ignoreEmailVerification = EMAIL_VERIFICATION_IGNORE_PATHS.stream().anyMatch(requestURI::contains);

		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		User user = authenticationService.authUser(accessToken, ignoreEmailVerification, false);

		request.setAttribute(AttributesConstants.USER, user);
		request.setAttribute(AttributesConstants.ACCESS_TOKEN, accessToken);

		log.info("Authenticated user ({}, {}) successfully", user.getUsername(), user.getEmail());
		return true;
	}
}
