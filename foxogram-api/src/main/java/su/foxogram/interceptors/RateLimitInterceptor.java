package su.foxogram.interceptors;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.RateLimitConstants;
import su.foxogram.exceptions.api.RateLimitExceededException;
import su.foxogram.util.PenaltyBucket;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	private final Map<String, PenaltyBucket> clients = new ConcurrentHashMap<>();

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws RateLimitExceededException {
		String clientRemoteAddr = request.getHeader("X-Forwarded-For");
		if (clientRemoteAddr == null) clientRemoteAddr = request.getRemoteAddr();
		PenaltyBucket bucket = clients.computeIfAbsent(clientRemoteAddr, this::createNewBucket);

		if (bucket.tryConsume(RateLimitConstants.RATE_LIMIT_CONSUME)) {
			return true;
		}
		else {
			bucket.tryConsume(RateLimitConstants.RATE_LIMIT_CONSUME);

			String unlockingDate = bucket.getUnlockingTime().toString();
			long availableTokens = bucket.getAvailableTokens();
			long msToRefill = bucket.getMsToRefill(RateLimitConstants.RATE_LIMIT_CONSUME);

			log.info("Rate-limited client ({}, {}, {}, {}) successfully", clientRemoteAddr, availableTokens, msToRefill, unlockingDate);
			throw new RateLimitExceededException(msToRefill);
		}
	}

	private PenaltyBucket createNewBucket(String clientRemoteAddr) {
		Bandwidth bandwidth = Bandwidth.builder().capacity(RateLimitConstants.RATE_LIMIT_CAPACITY).refillIntervally(RateLimitConstants.RATE_LIMIT_REFILL, Duration.ofMinutes(RateLimitConstants.RATE_LIMIT_DURATION)).build();

		Bucket bucket = Bucket.builder()
				.addLimit(bandwidth)
				.build();

		return new PenaltyBucket(bucket,
				Duration.ofMinutes(RateLimitConstants.RATE_LIMIT_DURATION).getSeconds());
	}
}
