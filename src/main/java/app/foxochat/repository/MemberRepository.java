package app.foxochat.repository;

import app.foxochat.model.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {

    Optional<Member> findByChannelIdAndUserId(long channelId, long userId);

    Flux<Member> findAllByUserId(long userId);

    Flux<Member> findAllByChannelId(long channelId);
}
