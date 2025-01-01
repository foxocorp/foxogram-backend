package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {
	Channel findByIcon(String hash);

	Channel findByName(String name);

	@Override
	void delete(@NotNull Channel channel);
}
