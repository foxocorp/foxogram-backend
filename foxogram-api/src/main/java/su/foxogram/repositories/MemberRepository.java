package su.foxogram.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {

	Optional<Member> findByChannelIdAndUserId(long channelId, long userId);

	List<Member> findAllByUserId(long userId);

	List<Member> findAllByChannelId(long channelId);
}
