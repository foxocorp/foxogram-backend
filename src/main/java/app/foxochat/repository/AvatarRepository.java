package app.foxochat.repository;

import app.foxochat.model.Avatar;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends ReactiveCrudRepository<Avatar, Long> {

    Optional<Avatar> findById(long id);
}
