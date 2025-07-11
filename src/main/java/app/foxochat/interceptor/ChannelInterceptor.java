package app.foxochat.interceptor;

import app.foxochat.constant.AttributeConstant;
import app.foxochat.constant.ChannelConstant;
import app.foxochat.exception.channel.ChannelNotFoundException;
import app.foxochat.model.Member;
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
import reactor.core.publisher.Mono;

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
    ) throws ChannelNotFoundException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;

        String uri = request.getRequestURI();
        Matcher matcher = CHANNEL_ID_PATTERN.matcher(uri);

        if (!matcher.find()) {
            throw new ChannelNotFoundException();
        }

        long id = Long.parseLong(matcher.group(1));
        channelService.getById(id).switchIfEmpty(Mono.error(ChannelNotFoundException::new)).expand(c -> {
                    User user = (User) request.getAttribute(AttributeConstant.USER);

                    if (!c.hasFlag(ChannelConstant.Flags.PUBLIC) && c.getMembers().stream()
                            .noneMatch(u -> u.getUser().getId() == user.getId())) {
                        return Mono.error(ChannelNotFoundException::new);
                    }

                    Member member;
                    try {
                        member = memberService.getByChannelIdAndUserId(c.getId(), user.getId())
                                .orElseThrow(ChannelNotFoundException::new);
                    } catch (ChannelNotFoundException e) {
                        return Mono.error(ChannelNotFoundException::new);
                    }

                    request.setAttribute(AttributeConstant.MEMBER, member);
                    request.setAttribute(AttributeConstant.CHANNEL, c);

                    log.debug("Got channel {} successfully", c.getId());
                    return Mono.empty();
                })
                .subscribe();


        return true;
    }
}
