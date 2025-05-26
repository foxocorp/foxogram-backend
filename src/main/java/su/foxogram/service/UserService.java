package su.foxogram.service;

import su.foxogram.constant.UserConstant;
import su.foxogram.dto.api.request.UserEditDTO;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.exception.user.UserContactAlreadyExistException;
import su.foxogram.exception.user.UserContactNotFoundException;
import su.foxogram.exception.user.UserCredentialsDuplicateException;
import su.foxogram.exception.user.UserCredentialsIsInvalidException;
import su.foxogram.model.User;

import java.util.Optional;

public interface UserService {

	Optional<User> getById(long id);

	Optional<User> getByUsername(String username);

	Optional<User> getByEmail(String email);

	void updateFlags(User user, UserConstant.Flags removeFlag, UserConstant.Flags addFlag);

	User add(String username, String email, String password) throws UserCredentialsDuplicateException;

	User update(User user, UserEditDTO body) throws UserCredentialsDuplicateException, UnknownAttachmentsException;

	void requestDelete(User user, String password) throws UserCredentialsIsInvalidException;

	void confirmDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException;

	void setStatus(long userId, int status) throws Exception;

	User addContact(User user, long id) throws UserContactAlreadyExistException;

	void deleteContact(User user, long id) throws UserContactNotFoundException;
}
