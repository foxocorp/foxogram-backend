package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;

import java.util.List;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {
	@Query(value = "SELECT m FROM Member m WHERE m.channel = :ch AND m.user = :usr")
	Member findByChannelAndUser(@Param("ch") Channel channel, @Param("usr") User user);

	@Query(value = "SELECT m FROM Member m WHERE m.channel = :ch AND m.user.username = :username")
	Member findByChannelAndUsername(@Param("ch") Channel channel, @Param("username") String username);

	@Query(value = "SELECT m FROM Member m WHERE m.channel = :ch AND m.user.id = :id")
	Member findByChannelAndId(@Param("ch") Channel channel, @Param("id") long id);

	List<Member> findAllByUserId(long userId);

	List<Member> findAllByChannel(Channel channel);

	@Override
	void delete(@NotNull Member member);
}
