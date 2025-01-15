package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;

import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {
	Channel findByIcon(String hash);

	Channel findByName(String name);

	Optional<Channel> findByNameOrId(String name, long id);

	@Override
	void delete(@NotNull Channel channel);
}
