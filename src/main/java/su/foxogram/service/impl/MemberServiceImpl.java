package su.foxogram.service.impl;

import org.springframework.stereotype.Service;
import su.foxogram.model.Channel;
import su.foxogram.model.Member;
import su.foxogram.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements su.foxogram.service.MemberService {

	private final MemberRepository memberRepository;

	public MemberServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public List<Channel> getChannelsByUserId(long userId) {
		return memberRepository.findAllByUserId(userId)
				.stream()
				.map(Member::getChannel)
				.collect(Collectors.toList());
	}

	@Override
	public List<Member> getAllByChannelId(long channelId) {
		return memberRepository.findAllByChannelId(channelId);
	}

	@Override
	public Optional<Member> getByChannelIdAndUserId(long channelId, long userId) {
		return memberRepository.findByChannelIdAndUserId(channelId, userId);
	}

	@Override
	public Member add(Member member) {
		return memberRepository.save(member);
	}

	@Override
	public void delete(Member member) {
		memberRepository.delete(member);
	}
}
