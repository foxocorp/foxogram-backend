package su.foxogram.service;

import su.foxogram.model.Channel;
import su.foxogram.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberService {
	List<Channel> getChannelsByUserId(long userId);

	List<Member> getAllByChannelId(long channelId);

	Optional<Member> getByChannelIdAndUserId(long channelId, long userId);

	Member add(Member member);

	void delete(Member member);
}
