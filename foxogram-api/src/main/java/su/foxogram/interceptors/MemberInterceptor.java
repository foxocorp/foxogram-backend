package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.services.ChannelsService;

import java.util.Objects;

@Component
public class MemberInterceptor implements HandlerInterceptor {

	private final ChannelsService channelsService;

	@Autowired
	public MemberInterceptor(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ChannelNotFoundException {
		if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) return true;

		User user = (User) request.getAttribute(AttributesConstants.USER);
		Channel channel = (Channel) request.getAttribute(AttributesConstants.CHANNEL);

		Member member = channelsService.getMember(channel, user.getId());

		if (member == null) throw new ChannelNotFoundException();

		request.setAttribute(AttributesConstants.MEMBER, member);
		return true;
	}
}
