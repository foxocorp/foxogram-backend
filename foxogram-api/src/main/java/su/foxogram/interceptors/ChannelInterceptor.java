package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.services.ChannelsService;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class ChannelInterceptor implements HandlerInterceptor {

	private final ChannelsService channelsService;

	@Autowired
	public ChannelInterceptor(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ChannelNotFoundException {
		if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) return true;

		@SuppressWarnings("unchecked")
		Map<String, String> uriVariables = (Map<String, String>) getUriVariables(request);

		String channelKey = getChannelKey(uriVariables).orElseThrow(ChannelNotFoundException::new);

		request.setAttribute(AttributesConstants.CHANNEL, channelsService.getChannel(channelKey));

		return true;
	}

	private Map<?, ?> getUriVariables(HttpServletRequest request) {
		return Optional.ofNullable(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
				.filter(Map.class::isInstance)
				.map(Map.class::cast)
				.orElseGet(Collections::emptyMap);
	}

	private Optional<String> getChannelKey(Map<String, String> uriVariables) {
		String channelId = uriVariables.get("idOrName");

		try {
			return Optional.of(channelId);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
}
