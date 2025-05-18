package su.foxogram.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findById(long id);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);
}
