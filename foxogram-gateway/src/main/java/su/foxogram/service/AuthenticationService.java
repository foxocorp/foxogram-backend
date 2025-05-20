package su.foxogram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import su.foxogram.dto.api.response.UserDTO;
import su.foxogram.exception.user.UserUnauthorizedException;

import java.util.Objects;

@Slf4j
@Service
public class AuthenticationService {

	private final RestClient restClient;

	public AuthenticationService(RestClient restClient) {
		this.restClient = restClient;
	}

	public Long authenticate(String token) throws UserUnauthorizedException {
		try {
			UserDTO user = restClient.get()
					.uri("/users/@me")
					.header("Authorization", "Bearer " + token)
					.retrieve()
					.body(UserDTO.class);

			return Objects.requireNonNull(user).getId();
		} catch (RestClientResponseException e) {
			log.error("{}: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		throw new UserUnauthorizedException();
	}
}
