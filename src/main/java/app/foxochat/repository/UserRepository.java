package app.foxochat.repository;

import app.foxochat.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findById(long id);

    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);
}
