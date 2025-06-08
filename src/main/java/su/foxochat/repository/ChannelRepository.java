package su.foxochat.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxochat.model.Channel;

import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {

	Optional<Channel> findById(long id);

	Optional<Channel> findByName(String name);
}
