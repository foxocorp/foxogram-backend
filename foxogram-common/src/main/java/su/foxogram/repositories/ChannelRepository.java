package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;

import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {
	Optional<Channel> findById(long id);

	Optional<Channel> findByName(String name);

	@Override
	void delete(@NotNull Channel channel);
}
