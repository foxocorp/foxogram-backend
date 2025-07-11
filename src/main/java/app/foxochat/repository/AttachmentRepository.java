package app.foxochat.repository;

import app.foxochat.model.Attachment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends ReactiveCrudRepository<Attachment, Long> {

    Optional<Attachment> findById(long id);
}
