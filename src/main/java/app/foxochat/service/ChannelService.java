package app.foxochat.service;

import app.foxochat.dto.api.request.ChannelCreateDTO;
import app.foxochat.dto.api.request.ChannelEditDTO;
import app.foxochat.exception.channel.ChannelAlreadyExistException;
import app.foxochat.exception.channel.ChannelNotFoundException;
import app.foxochat.exception.user.UserNotFoundException;
import app.foxochat.model.Channel;
import app.foxochat.model.Member;
import app.foxochat.model.User;

import java.util.concurrent.CompletableFuture;

public interface ChannelService {
    Channel add(User user, long partnerId, ChannelCreateDTO body)
            throws ChannelAlreadyExistException, UserNotFoundException;

    CompletableFuture<Channel> getById(long id) throws ChannelNotFoundException;

    Channel getByName(String name) throws ChannelNotFoundException;

    Channel update(Member member, Channel channel, ChannelEditDTO body) throws Exception;

    void delete(Channel channel, User user) throws Exception;

    void addMember(Channel channel, User user) throws Exception;

    void removeMember(Channel channel, User user) throws Exception;
}
