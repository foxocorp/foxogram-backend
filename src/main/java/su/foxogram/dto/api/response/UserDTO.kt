package su.foxogram.dto.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter
import su.foxogram.model.User

@Getter
@Setter
@Schema(name = "User")
data class UserDTO(
    var user: User,
    val channelsList: MutableList<Long>?,
    val contactsList: MutableList<Long>?,
    val includeEmail: Boolean,
    val includeChannels: Boolean,
    val includeContacts: Boolean
) {
    private var id = user.id

    private var avatar: AttachmentDTO? = AttachmentDTO(user.avatar)

    private var displayName = user.displayName

    private var username = user.username

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private var email = user.email

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private var channels = channelsList

    private var status = user.status

    private var statusUpdatedAt = user.statusUpdatedAt

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private var contacts = contactsList

    private var flags = user.flags

    private var type = user.type

    private var createdAt = user.createdAt
}
