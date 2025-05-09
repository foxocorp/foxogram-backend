package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.repositories.MemberRepository;

import java.util.List;
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
}
