package su.foxogram.dto.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter
import su.foxogram.model.Member

@Getter
@Setter
@Schema(name = "Member")
data class MemberDTO(val member: Member, val includeChannel: Boolean) {
    val id: Long = member.id

    val user: UserDTO = UserDTO(
        member.user, null, null,
        includeEmail = false,
        includeChannels = false,
        includeContacts = false
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var channel: ChannelDTO? = if (includeChannel) ChannelDTO(member.channel, null) else null

    val permissions: Long = member.permissions

    val joinedAt: Long = member.joinedAt
}

