package app.foxochat.interceptor;

import app.foxochat.constant.AttributeConstant;
import app.foxochat.constant.ChannelConstant;
import app.foxochat.exception.channel.ChannelNotFoundException;
import app.foxochat.model.Channel;
import app.foxochat.model.User;
import app.foxochat.service.ChannelService;
import app.foxochat.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChannelInterceptor implements AsyncHandlerInterceptor {

    private static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("/channels/(\\d+)");

    private final ChannelService channelService;

    private final MemberService memberService;

    public ChannelInterceptor(ChannelService channelService, MemberService memberService) {
        this.channelService = channelService;
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws ChannelNotFoundException, ExecutionException, InterruptedException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;

        String uri = request.getRequestURI();
        Matcher matcher = CHANNEL_ID_PATTERN.matcher(uri);

        if (!matcher.find()) {
            throw new ChannelNotFoundException();
        }

        long id = Long.parseLong(matcher.group(1));
        Channel channel = channelService.getById(id).get();

        User user = (User) request.getAttribute(AttributeConstant.USER);

        if (!channel.hasFlag(ChannelConstant.Flags.PUBLIC) && memberService.getByChannelIdAndUserId(channel.getId(),
                user.getId()).get().isEmpty()) {
            throw new ChannelNotFoundException();
        }

        request.setAttribute(AttributeConstant.CHANNEL, channel);

        log.debug("Got channel {} successfully", channel.getId());
        return true;
    }
}
