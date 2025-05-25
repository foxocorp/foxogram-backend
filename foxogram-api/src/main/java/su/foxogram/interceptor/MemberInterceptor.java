package su.foxogram.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constant.AttributeConstant;
import su.foxogram.exception.channel.ChannelNotFoundException;
import su.foxogram.model.Channel;
import su.foxogram.model.Member;
import su.foxogram.model.User;
import su.foxogram.service.MemberService;

import java.util.Objects;

@Slf4j
@Component
public class MemberInterceptor implements HandlerInterceptor {

	private final MemberService memberService;

	@Autowired
	public MemberInterceptor(MemberService memberService) {
		this.memberService = memberService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws ChannelNotFoundException {
		if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) return true;

		if (Objects.equals(request.getMethod(), HttpMethod.PUT.name()) && request.getRequestURI().matches("/channels/\\d+/members/@me")) {
			return true;
		}

		User user = (User) request.getAttribute(AttributeConstant.USER);
		Channel channel = (Channel) request.getAttribute(AttributeConstant.CHANNEL);

		Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId())
				.orElseThrow(ChannelNotFoundException::new);

		request.setAttribute(AttributeConstant.MEMBER, member);

		log.debug("Got member {} in channel {} successfully", member.getId(), channel.getId());
		return true;
	}
}
