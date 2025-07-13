package app.foxochat.repository;

import app.foxochat.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByChannelIdAndUserId(long channelId, long userId);

    List<Member> findAllByUserId(long userId);

    List<Member> findAllByChannelId(long channelId);
}
