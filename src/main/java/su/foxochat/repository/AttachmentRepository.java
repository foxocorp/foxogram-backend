package su.foxochat.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxochat.model.Attachment;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

	Optional<Attachment> findById(long id);
}
