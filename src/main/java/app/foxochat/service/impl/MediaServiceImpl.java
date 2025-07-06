package app.foxochat.service.impl;

import app.foxochat.constant.MediaConstant;
import app.foxochat.constant.StorageConstant;
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
import app.foxochat.repository.AttachmentRepository;
import app.foxochat.repository.AvatarRepository;
import app.foxochat.service.MediaService;
import app.foxochat.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MediaServiceImpl implements MediaService {

    public final AttachmentRepository attachmentRepository;

    public final StorageService storageService;

    private final AvatarRepository avatarRepository;

    public MediaServiceImpl(AttachmentRepository attachmentRepository, StorageService storageService, AvatarRepository avatarRepository) {
        this.attachmentRepository = attachmentRepository;
        this.storageService = storageService;
        this.avatarRepository = avatarRepository;
    }

    @Override
    public MediaPresignedURLDTO getPresignedURLAndSave(Class<?> media, User user, Channel channel, long flags)
            throws UploadFailedException {
        if (media == AttachmentUploadDTO.class) {
            try {
                AttachmentUploadDTO attachment = (AttachmentUploadDTO) media.getDeclaredConstructor().newInstance();
                MediaPresignedURLDTO dto = storageService.getPresignedUrl(StorageConstant.ATTACHMENTS_BUCKET);
                Attachment obj = attachmentRepository.save(new Attachment(user,
                        dto.getUuid(),
                        attachment.getFilename(),
                        attachment.getContentType(),
                        flags));

                log.debug("Successfully got presigned url and saved attachment {}", dto.getUuid());
                return new MediaPresignedURLDTO(dto.getUrl(), dto.getUuid(), obj.getClass());
            } catch (Exception e) {
                throw new UploadFailedException();
            }
        } else if (media == AvatarUploadDTO.class) {
            try {
                AvatarUploadDTO avatar = (AvatarUploadDTO) media.getDeclaredConstructor().newInstance();
                MediaPresignedURLDTO dto = storageService.getPresignedUrl(StorageConstant.AVATARS_BUCKET);
                boolean isUser = false;
                boolean isChannel = false;

                if (user != null) isUser = true;
                if (channel != null) isChannel = true;

                Avatar obj = avatarRepository.save(new Avatar(user,
                        channel,
                        dto.getUuid(),
                        avatar.getFilename(),
                        isUser,
                        isChannel));
                return new MediaPresignedURLDTO(dto.getUrl(), dto.getUuid(), obj.getClass());
            } catch (Exception e) {
                throw new UploadFailedException();
            }
        } else throw new UploadFailedException();
    }

//	@Override
//	public List<MediaUploadDTO> uploadAll(User user, List<AttachmentUploadDTO> attachments) {
//		List<MediaUploadDTO> attachmentsData = new ArrayList<>();
//
//		attachments.forEach(attachment -> {
//			MediaPresignedURLDTO dto = getPresignedURLAndSave(attachment, user);
//			attachmentsData.add(new MediaUploadDTO(dto.getUrl(), dto.getAttachment().getId()));
//		});
//
//		log.debug("Successfully uploaded all media by user {}", user.getUsername());
//		return attachmentsData;
//	}

    @Override
    public MediaPresignedURLDTO uploadAvatar(User user, Channel channel, AvatarUploadDTO avatar)
            throws MediaCannotBeEmptyException, UnknownMediaException, UploadFailedException {
        if (avatar == null) throw new MediaCannotBeEmptyException();

        MediaPresignedURLDTO dto = getPresignedURLAndSave(avatar.getClass(), user, channel, 0);

        Avatar media;
        try {
            media = (Avatar) dto.getMedia().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new UploadFailedException();
        }

        if (user != null && media.getUser().getId() != user.getId() || channel != null && media.getChannel()
                .getId() != channel.getId()) {
            throw new UnknownMediaException();
        }

        return dto;
    }

    @Override
    public List<MediaUploadDTO> uploadAttachments(User user, List<AttachmentUploadDTO> attachments)
            throws MediaCannotBeEmptyException {
        if (attachments == null || attachments.isEmpty()) throw new MediaCannotBeEmptyException();

        List<MediaUploadDTO> uploaded = new ArrayList<>();

        attachments.forEach(attachment -> {
            try {
                MediaPresignedURLDTO dto = getPresignedURLAndSave(attachment.getClass(),
                        user,
                        null,
                        attachment.isSpoiler() ? MediaConstant.Flags.SPOILER.getBit() : 0);

                Attachment media;
                try {
                    media = (Attachment) dto.getMedia().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new UploadFailedException();
                }

                if (user != null && media.getUser().getId() != user.getId()) {
                    throw new UnknownMediaException();
                }

                MediaUploadDTO urlDTO = new MediaUploadDTO(dto.getUrl(), media.getId());

                log.debug("Successfully uploaded attachment by user {}", user.getUsername());
                uploaded.add(urlDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return uploaded;
    }

    @Override
    public Avatar getAvatar(User user, Channel channel, long id) throws UnknownMediaException {
        Avatar avatar = avatarRepository.findById(id).orElseThrow(UnknownMediaException::new);

        if (avatar.getUser().getId() != user.getId()) throw new UnknownMediaException();
        else if (avatar.getChannel().getId() != channel.getId()) throw new UnknownMediaException();

        log.debug("Successfully got avatar for user {}, or channel {}", user.getUsername(), channel.getName());
        return avatar;
    }

    @Override
    public List<Attachment> getAttachments(User user, List<Long> attachmentsIds) throws UnknownMediaException {
        List<Attachment> attachments = new ArrayList<>();

        if (!attachmentsIds.isEmpty()) {
            for (Long id : attachmentsIds) {
                Attachment attachment = attachmentRepository.findById(id).orElseThrow(UnknownMediaException::new);

                if (attachment.getUser().getId() != user.getId()) throw new UnknownMediaException();

                attachments.add(attachment);
            }
        }

        log.debug("Successfully got all attachments by user {}", user.getUsername());
        return attachments;
    }

    @Override
    public Avatar getAvatarById(long id) throws UnknownMediaException {
        return avatarRepository.findById(id).orElseThrow(UnknownMediaException::new);
    }

    @Override
    public Attachment getAttachmentById(long id) throws UnknownMediaException {
        return attachmentRepository.findById(id).orElseThrow(UnknownMediaException::new);
    }
}
