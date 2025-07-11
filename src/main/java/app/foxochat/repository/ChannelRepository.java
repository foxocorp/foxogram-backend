package app.foxochat.repository;

import app.foxochat.model.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ChannelRepository extends ReactiveCrudRepository<Channel, Long> {

    Mono<Channel> findById(long id);

    Mono<Channel> findByName(String name);
}
