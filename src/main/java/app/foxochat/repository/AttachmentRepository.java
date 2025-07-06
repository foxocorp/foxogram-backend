package app.foxochat.repository;

import app.foxochat.model.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

    Optional<Attachment> findById(long id);
}
