package su.foxochat.service;

import su.foxochat.dto.api.request.ChannelCreateDTO;
import su.foxochat.dto.api.request.ChannelEditDTO;
import su.foxochat.exception.channel.ChannelAlreadyExistException;
import su.foxochat.exception.channel.ChannelNotFoundException;
import su.foxochat.model.Channel;
import su.foxochat.model.Member;
import su.foxochat.model.User;

public interface ChannelService {
	Channel add(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException;

	Channel getById(long id) throws ChannelNotFoundException;

	Channel getByName(String name) throws ChannelNotFoundException;

	Channel update(Member member, Channel channel, ChannelEditDTO body) throws Exception;

	void delete(Channel channel, User user) throws Exception;

	Member addMember(Channel channel, User user) throws Exception;

	void removeMember(Channel channel, User user) throws Exception;
}
