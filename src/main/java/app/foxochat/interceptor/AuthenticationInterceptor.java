package app.foxochat.interceptor;

import app.foxochat.config.APIConfig;
import app.foxochat.constant.AttributeConstant;
import app.foxochat.exception.user.UserEmailNotVerifiedException;
import app.foxochat.exception.user.UserUnauthorizedException;
import app.foxochat.model.User;
import app.foxochat.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class AuthenticationInterceptor implements AsyncHandlerInterceptor {

    private static final Set<String> EMAIL_VERIFICATION_IGNORE_PATHS = Set.of(
            "/auth/email/verify",
            "/users/@me",
            "/auth/email/resend"
    );

    final AuthenticationService authenticationService;

    final APIConfig apiConfig;

    public AuthenticationInterceptor(AuthenticationService authenticationService, APIConfig apiConfig) {
        this.authenticationService = authenticationService;
        this.apiConfig = apiConfig;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws UserUnauthorizedException, UserEmailNotVerifiedException, ExecutionException, InterruptedException {
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) return true;

        String requestURI = request.getRequestURI();
        boolean ignoreEmailVerification = EMAIL_VERIFICATION_IGNORE_PATHS.stream().anyMatch(requestURI::contains);
        if (apiConfig.isDevelopment()) ignoreEmailVerification = true;

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        User user = authenticationService.authUser(accessToken, ignoreEmailVerification).get();

        request.setAttribute(AttributeConstant.USER, user);
        request.setAttribute(AttributeConstant.ACCESS_TOKEN, accessToken);

        log.debug("Authenticated user {} successfully", user.getUsername());
        return true;
    }
}
