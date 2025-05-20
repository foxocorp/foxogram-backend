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
import su.foxogram.service.ChannelService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChannelInterceptor implements HandlerInterceptor {

	private final ChannelService channelService;

	private static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("/channels/(\\d+)");

	@Autowired
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
		request.setAttribute(AttributeConstant.CHANNEL, channelService.getById(id));

		return true;
	}
}
