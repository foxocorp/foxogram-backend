package app.foxochat.service;

import app.foxochat.constant.UserConstant;
import app.foxochat.dto.api.request.UserEditDTO;
import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.exception.user.UserContactAlreadyExistException;
import app.foxochat.exception.user.UserContactNotFoundException;
import app.foxochat.exception.user.UserCredentialsDuplicateException;
import app.foxochat.exception.user.UserCredentialsIsInvalidException;
import app.foxochat.model.User;

import java.util.Optional;

public interface UserService {

	Optional<User> getById(long id);

	Optional<User> getByUsername(String username);

	Optional<User> getByEmail(String email);

	void updateFlags(User user, UserConstant.Flags removeFlag, UserConstant.Flags addFlag);

	User add(String username, String email, String password) throws UserCredentialsDuplicateException;

	User update(User user, UserEditDTO body) throws Exception;

	void requestDelete(User user, String password) throws UserCredentialsIsInvalidException;

	void confirmDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException;

	void setStatus(long userId, int status) throws Exception;

	User addContact(User user, long id) throws UserContactAlreadyExistException;

	void deleteContact(User user, long id) throws UserContactNotFoundException;

	void save(User user);
}
