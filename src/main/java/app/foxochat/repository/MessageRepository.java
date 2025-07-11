package app.foxochat.repository;

import app.foxochat.model.Channel;
import app.foxochat.model.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.channel = :ch AND m.timestamp < :before ORDER BY m.id DESC LIMIT :limit")
    Flux<Message> findAllByChannel(@Param("ch") Channel channel, @Param("before") long before,
                                   @Param("limit") int limit);

    @Query("SELECT m FROM Message m WHERE m.channel = :ch AND m.id = :id")
    Optional<Message> findByChannelAndId(@Param("ch") Channel channel, @Param("id") long id);

    @Query("SELECT m FROM Message m WHERE m.channel = :ch ORDER BY m.id DESC LIMIT 1")
    Optional<Message> getLastMessageByChannel(@Param("ch") Channel channel);

    @NonNull
    Flux<Message> findAll();
}
