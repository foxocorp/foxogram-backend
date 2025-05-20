package su.foxogram.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Attachment;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

	Optional<Attachment> findById(long id);
}
