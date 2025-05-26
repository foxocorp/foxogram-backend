package su.foxogram.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.model.Channel;

import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {

	Optional<Channel> findById(long id);

	Optional<Channel> findByName(String name);
}
