package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import su.foxogram.dtos.api.response.UserDTO;
import su.foxogram.exceptions.user.UserUnauthorizedException;

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
			throw new UserUnauthorizedException();
		}
	}
}
