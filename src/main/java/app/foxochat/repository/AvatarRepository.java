package app.foxochat.repository;

import app.foxochat.model.Avatar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends CrudRepository<Avatar, Long> {

    Optional<Avatar> findById(long id);
}
