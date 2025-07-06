package app.foxochat.service;

import app.foxochat.dto.api.request.AttachmentUploadDTO;
import app.foxochat.dto.api.request.AvatarUploadDTO;
import app.foxochat.dto.api.response.MediaUploadDTO;
import app.foxochat.dto.internal.MediaPresignedURLDTO;
import app.foxochat.exception.media.MediaCannotBeEmptyException;
import app.foxochat.exception.media.UnknownMediaException;
import app.foxochat.exception.media.UploadFailedException;
import app.foxochat.model.Attachment;
import app.foxochat.model.Avatar;
import app.foxochat.model.Channel;
import app.foxochat.model.User;

import java.util.List;

public interface MediaService {

    MediaPresignedURLDTO getPresignedURLAndSave(AttachmentUploadDTO attachment, AvatarUploadDTO avatar,
                                                User user,
                                                Channel channel,
                                                long flags) throws UploadFailedException;

    MediaPresignedURLDTO uploadAvatar(User user, Channel channel, AvatarUploadDTO avatar)
            throws MediaCannotBeEmptyException, UnknownMediaException, UploadFailedException;

    List<MediaUploadDTO> uploadAttachments(User user, List<AttachmentUploadDTO> attachments)
            throws MediaCannotBeEmptyException;

    Avatar getAvatar(User user, Channel channel, long id) throws UnknownMediaException;

    List<Attachment> getAttachments(User user, List<Long> attachmentsIds) throws UnknownMediaException;

    Avatar getAvatarById(long id) throws UnknownMediaException;

    Attachment getAttachmentById(long id) throws UnknownMediaException;
}
