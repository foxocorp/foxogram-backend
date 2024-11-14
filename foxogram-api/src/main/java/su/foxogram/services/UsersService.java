package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.models.User;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.repositories.UserRepository;

@Service
public class UsersService {

	private final UserRepository userRepository;

    @Autowired
	public UsersService(UserRepository userRepository, AuthenticationService authenticationService) {
		this.userRepository = userRepository;
    }

	public User getUser(long id) throws UserUnauthorizedException {
		User user = userRepository.findById(id);

		if (user == null) {
			throw new UserUnauthorizedException();
		}

		return user;
	}

	public User editUser(long id) {
		return null;
	}
}
