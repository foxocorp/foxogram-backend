package su.foxochat.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxochat.model.OTP;

import java.util.Optional;

@Repository
public interface OTPRepository extends CrudRepository<OTP, Long> {

	Optional<OTP> findByUserId(long userId);

	Optional<OTP> findByValue(String value);
}
