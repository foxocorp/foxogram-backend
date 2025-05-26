package su.foxogram.service;

import su.foxogram.dto.api.request.ChannelCreateDTO;
import su.foxogram.dto.api.request.ChannelEditDTO;
import su.foxogram.exception.channel.ChannelAlreadyExistException;
import su.foxogram.exception.channel.ChannelNotFoundException;
import su.foxogram.model.Channel;
import su.foxogram.model.Member;
import su.foxogram.model.User;

public interface ChannelService {
	Channel add(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException;

	Channel getById(long id) throws ChannelNotFoundException;

	Channel getByName(String name) throws ChannelNotFoundException;

	Channel update(Member member, Channel channel, ChannelEditDTO body) throws Exception;

	void delete(Channel channel, User user) throws Exception;

	Member addMember(Channel channel, User user) throws Exception;

	void removeMember(Channel channel, User user) throws Exception;
}
