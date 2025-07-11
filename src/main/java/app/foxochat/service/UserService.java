package app.foxochat.service;

import app.foxochat.constant.UserConstant;
import app.foxochat.dto.api.request.UserEditDTO;
import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.exception.user.ContactAlreadyExistException;
import app.foxochat.exception.user.ContactNotFoundException;
import app.foxochat.exception.user.UserCredentialsDuplicateException;
import app.foxochat.exception.user.UserCredentialsIsInvalidException;
import app.foxochat.model.User;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserService {

    Optional<User> getById(long id);

    Optional<User> getByUsername(String username);

    Optional<User> getByEmail(String email);

    void updateFlags(User user, UserConstant.Flags removeFlag, UserConstant.Flags addFlag);

    Mono<User> add(String username, String email, String password) throws UserCredentialsDuplicateException;

    User update(User user, UserEditDTO body) throws Exception;

    void requestDelete(User user, String password) throws UserCredentialsIsInvalidException;

    void confirmDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException;

    void setStatus(long userId, int status) throws Exception;

    User addContact(User user, long id) throws ContactAlreadyExistException;

    void deleteContact(User user, long id) throws ContactNotFoundException;

    void save(User user);
}
