package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.services.ChannelsService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChannelInterceptor implements HandlerInterceptor {

	private final ChannelsService channelsService;

	private static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("/channels/(\\d+)");

	@Autowired
	public ChannelInterceptor(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ChannelNotFoundException {
		if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;

		String uri = request.getRequestURI();
		Matcher matcher = CHANNEL_ID_PATTERN.matcher(uri);

		if (!matcher.find()) {
			throw new ChannelNotFoundException();
		}

		long id = Long.parseLong(matcher.group(1));
		request.setAttribute(AttributesConstants.CHANNEL, channelsService.getChannelById(id));

		return true;
	}
}
