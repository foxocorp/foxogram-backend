package su.foxogram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.model.Channel;
import su.foxogram.model.Member;
import su.foxogram.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {

	private final MemberRepository memberRepository;

	@Autowired
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public List<Channel> getChannelsByUserId(long userId) {
		return memberRepository.findAllByUserId(userId)
				.stream()
				.map(Member::getChannel)
				.collect(Collectors.toList());
	}

	public List<Member> getAllByChannelId(long channelId) {
		return memberRepository.findAllByChannelId(channelId);
	}

	public Optional<Member> getByChannelIdAndUserId(long channelId, long userId) {
		return memberRepository.findByChannelIdAndUserId(channelId, userId);
	}

	public Member add(Member member) {
		return memberRepository.save(member);
	}

	public void delete(Member member) {
		memberRepository.delete(member);
	}
}
