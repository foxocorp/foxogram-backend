package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;

import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {
	Channel findByIcon(String hash);

	Optional<Channel> findById(long id);

	@Override
	void delete(@NotNull Channel channel);
}
