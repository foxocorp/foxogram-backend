package su.foxogram.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Attachment;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

	Attachment findById(long id);

	@Override
	void delete(@NotNull Attachment attachment);
}
