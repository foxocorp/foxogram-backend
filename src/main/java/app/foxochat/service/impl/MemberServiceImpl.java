package app.foxochat.service.impl;

import app.foxochat.model.Channel;
import app.foxochat.model.Member;
import app.foxochat.repository.MemberRepository;
import app.foxochat.service.MemberService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Caching(put = {
            @CachePut(value = "channelsByUserId", key = "#userId"),
    })
    public List<Channel> getChannelsByUserId(long userId) {
        return memberRepository.findAllByUserId(userId)
                .stream()
                .map(Member::getChannel)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(put = {
            @CachePut(value = "membersByChannelId", key = "#channelId"),
    })
    public List<Member> getAllByChannelId(long channelId) {
        return memberRepository.findAllByChannelId(channelId);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "membersByChannelId", key = "#channelId"),
            @CachePut(value = "membersByUserId", key = "#userId")
    })
    public Optional<Member> getByChannelIdAndUserId(long channelId, long userId) {
        return memberRepository.findByChannelIdAndUserId(channelId, userId);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "membersById", key = "#member.id")
    })
    public Member add(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public void delete(Member member) {
        memberRepository.delete(member);
    }
}
