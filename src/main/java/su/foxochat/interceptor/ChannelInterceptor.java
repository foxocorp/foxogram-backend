package su.foxochat.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxochat.constant.AttributeConstant;
import su.foxochat.constant.ChannelConstant;
import su.foxochat.exception.channel.ChannelNotFoundException;
import su.foxochat.model.Channel;
import su.foxochat.model.User;
import su.foxochat.service.ChannelService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChannelInterceptor implements HandlerInterceptor {

	private final ChannelService channelService;

	private static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("/channels/(\\d+)");

	public ChannelInterceptor(ChannelService channelService) {
		this.channelService = channelService;
	}

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws ChannelNotFoundException {
		if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;

		String uri = request.getRequestURI();
		Matcher matcher = CHANNEL_ID_PATTERN.matcher(uri);

		if (!matcher.find()) {
			throw new ChannelNotFoundException();
		}

		long id = Long.parseLong(matcher.group(1));
		Channel channel = channelService.getById(id);

		User user = (User) request.getAttribute(AttributeConstant.USER);

		if (!channel.hasFlag(ChannelConstant.Flags.PUBLIC) && channel.getMembers().stream().noneMatch(u -> u.getId() == user.getId())) {
			throw new ChannelNotFoundException();
		}

		request.setAttribute(AttributeConstant.CHANNEL, channel);

		log.debug("Got channel {} successfully", channel.getId());
		return true;
	}
}
