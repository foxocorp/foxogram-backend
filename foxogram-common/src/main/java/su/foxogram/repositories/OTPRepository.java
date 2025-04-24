package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.OTP;

@Repository
public interface OTPRepository extends CrudRepository<OTP, Long> {
	OTP findByUserId(long userId);

	OTP findByValue(String value);

	@Override
	void delete(@NotNull OTP OTP);
}
