package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.services.MemberService;

import java.util.Objects;

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

		User user = (User) request.getAttribute(AttributesConstants.USER);
		Channel channel = (Channel) request.getAttribute(AttributesConstants.CHANNEL);

		Member member = memberService.getByChannelAndUser(channel.getId(), user.getId())
				.orElseThrow(ChannelNotFoundException::new);

		request.setAttribute(AttributesConstants.MEMBER, member);
		return true;
	}
}
