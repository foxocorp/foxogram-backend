package su.foxogram.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.OTP;

import java.util.Optional;

@Repository
public interface OTPRepository extends CrudRepository<OTP, Long> {

	Optional<OTP> findByUserId(long userId);

	Optional<OTP> findByValue(String value);
}
