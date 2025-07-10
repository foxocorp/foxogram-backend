package app.foxochat.dto.api.response;

import app.foxochat.model.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Member")
public class MemberDTO {

    private long id;

    private long userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long channelId;

    private long permissions;

    private long joinedAt;

    public MemberDTO(Member member, boolean includeChannel) {
        this.id = member.getId();
        this.userId = member.getUser().getId();
        if (includeChannel)
            this.channelId = member.getChannel().getId();
        this.permissions = member.getPermissions();
        this.joinedAt = member.getJoinedAt();
    }
}

