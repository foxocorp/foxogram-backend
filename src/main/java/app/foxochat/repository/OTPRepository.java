package app.foxochat.repository;

import app.foxochat.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByUserId(long userId);

    Optional<OTP> findByValue(String value);
}
