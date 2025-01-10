package su.foxogram.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.constants.MemberConstants;
import su.foxogram.constants.StorageConstants;
import su.foxogram.dtos.api.request.ChannelCreateDTO;
import su.foxogram.dtos.api.request.ChannelEditDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.dtos.api.response.MemberDTO;
import su.foxogram.exceptions.cdn.UploadFailedException;
import su.foxogram.exceptions.channel.ChannelAlreadyExistException;
import su.foxogram.exceptions.channel.ChannelNotFoundException;
import su.foxogram.exceptions.member.MemberAlreadyInChannelException;
import su.foxogram.exceptions.member.MemberInChannelNotFoundException;
import su.foxogram.exceptions.member.MissingPermissionsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChannelsService {
	private final ChannelRepository channelRepository;

	private final MemberRepository memberRepository;

	private final UserRepository userRepository;

	private final StorageService storageService;

	private final ProducerKafkaService producerKafkaService;

	@Autowired
	public ChannelsService(ChannelRepository channelRepository, MemberRepository memberRepository, UserRepository userRepository, StorageService storageService, ProducerKafkaService producerKafkaService) {
		this.channelRepository = channelRepository;
		this.memberRepository = memberRepository;
		this.userRepository = userRepository;
		this.storageService = storageService;
		this.producerKafkaService = producerKafkaService;
	}

	public Channel createChannel(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException {
		String owner = user.getUsername();
		Channel channel;

		try {
			channel = new Channel(0, body.getDisplayName(), body.getName(), body.getType(), owner);
			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new ChannelAlreadyExistException();
		}

		user = userRepository.findByUsername(owner);

		Member member = new Member(user, channel, MemberConstants.Permissions.ADMIN.getBit());
		memberRepository.save(member);

		log.info("Channel ({}) by user ({}) created successfully", channel.getName(), user.getUsername());
		return channel;
	}

	public Channel getChannel(String name) throws ChannelNotFoundException {
		Channel channel = channelRepository.findByName(name);

		if (channel == null) throw new ChannelNotFoundException();

		return channel;
	}

	public Channel editChannel(Member member, Channel channel, ChannelEditDTO body) throws MissingPermissionsException, ChannelAlreadyExistException, JsonProcessingException {
		member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_CHANNEL);

		try {
			if (body.getIcon() != null) changeIcon(channel, body.getIcon());
			if (body.getDisplayName() != null) channel.setDisplayName(body.getDisplayName());
			if (body.getName() != null) channel.setName(body.getName());

			channelRepository.save(channel);
		} catch (DataIntegrityViolationException | UploadFailedException e) {
			throw new ChannelAlreadyExistException();
		}

		producerKafkaService.send(getRecipients(channel), new ChannelDTO(channel), GatewayConstants.Event.CHANNEL_UPDATE.getValue());
		log.info("Channel ({}) edited successfully", channel.getName());
		return channel;
	}

	public void deleteChannel(Channel channel, User user) throws MissingPermissionsException, JsonProcessingException {
		Member member = memberRepository.findByChannelAndUser(channel, user);

		member.hasAnyPermission(MemberConstants.Permissions.ADMIN);

		channelRepository.delete(channel);
		producerKafkaService.send(getRecipients(channel), Map.of("id", channel.getId()), GatewayConstants.Event.CHANNEL_DELETE.getValue());
		log.info("Channel ({}) deleted successfully", channel.getName());
	}

	public Member joinUser(Channel channel, User user) throws MemberAlreadyInChannelException, JsonProcessingException {
		Member member = memberRepository.findByChannelAndUsername(channel, user.getUsername());

		if (member != null) throw new MemberAlreadyInChannelException();

		user = userRepository.findByUsername(user.getUsername());

		member = new Member(user, channel, 0);
		log.info("Member ({}) joined channel ({}) successfully", member.getUser().getUsername(), channel.getName());
		producerKafkaService.send(getRecipients(channel), new MemberDTO(member), GatewayConstants.Event.MEMBER_ADD.getValue());
		return memberRepository.save(member);
	}

	public void leaveUser(Channel channel, User user) throws MemberInChannelNotFoundException, JsonProcessingException {
		Member member = memberRepository.findByChannelAndUser(channel, user);

		if (member == null) throw new MemberInChannelNotFoundException();

		member = memberRepository.findByChannelAndUser(channel, user);
		memberRepository.delete(member);
		producerKafkaService.send(getRecipients(channel), new MemberDTO(member), GatewayConstants.Event.MEMBER_REMOVE.getValue());
		log.info("Member ({}) left channel ({}) successfully", member.getUser().getUsername(), channel.getName());
	}

	public List<MemberDTO> getMembers(Channel channel) {
		return memberRepository.findAllByChannel(channel).stream()
				.map(MemberDTO::new)
				.toList();
	}

	public Member getMember(Channel channel, String memberUsername) {
		return memberRepository.findByChannelAndUsername(channel, memberUsername);
	}

	private void changeIcon(Channel channel, MultipartFile icon) throws UploadFailedException {
		String hash;

		try {
			hash = storageService.uploadToMinio(icon, StorageConstants.AVATARS_BUCKET);
		} catch (Exception e) {
			throw new UploadFailedException();
		}

		channel.setIcon(hash);
	}

	private List<Long> getRecipients(Channel channel) {
		channel = channelRepository.findById(channel.getId()).get();
		return channel.getMembers().stream()
				.map(Member::getId)
				.collect(Collectors.toList());
	}
}
