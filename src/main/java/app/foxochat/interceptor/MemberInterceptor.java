package app.foxochat.interceptor;

import app.foxochat.constant.AttributeConstant;
import app.foxochat.exception.channel.ChannelNotFoundException;
import app.foxochat.model.Channel;
import app.foxochat.model.Member;
import app.foxochat.model.User;
import app.foxochat.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class MemberInterceptor implements AsyncHandlerInterceptor {

    private final MemberService memberService;

    public MemberInterceptor(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws ChannelNotFoundException, ExecutionException, InterruptedException {
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) return true;

        if (Objects.equals(request.getMethod(), HttpMethod.PUT.name()) && request.getRequestURI()
                .matches("/channels/\\d+/members/@me")) {
            return true;
        }

        User user = (User) request.getAttribute(AttributeConstant.USER);
        Channel channel = (Channel) request.getAttribute(AttributeConstant.CHANNEL);

        Member member = memberService.getByChannelIdAndUserId(channel.getId(), user.getId()).get()
                .orElseThrow(ChannelNotFoundException::new);

        request.setAttribute(AttributeConstant.MEMBER, member);

        log.debug("Got member {} in channel {} successfully", member.getId(), channel.getId());
        return true;
    }
}
