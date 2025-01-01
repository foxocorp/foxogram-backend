package su.foxogram.util;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class PenaltyBucket {
	private final io.github.bucket4j.Bucket bucket;

	private final Long blockingTimeSeconds;

	private LocalDateTime unblockingDate;

	public PenaltyBucket(io.github.bucket4j.Bucket bucket, Long blockingTimeSeconds) {
		this.bucket = bucket;
		this.blockingTimeSeconds = blockingTimeSeconds;
		this.unblockingDate = now();
	}

	public boolean tryConsume(long numTokens) {
		if (unblockingDate.isBefore(now())
				&& bucket.tryConsume(numTokens)) {
			unblockingDate = now();
			return true;
		} else {
			if (!unblockingDate.isAfter(now())) {
				unblockingDate = now().plusSeconds(blockingTimeSeconds);
			}
			return false;
		}
	}

	public LocalDateTime getUnlockingTime() {
		return unblockingDate;
	}

	public long getAvailableTokens() {
		return bucket.getAvailableTokens();
	}

	public long getMsToRefill(long consume) {
		return bucket.estimateAbilityToConsume(consume).getNanosToWaitForRefill() / 10000000;
	}
}
