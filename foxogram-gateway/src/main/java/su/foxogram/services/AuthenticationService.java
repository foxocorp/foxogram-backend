package su.foxogram.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import su.foxogram.models.User;

@Service
public class AuthenticationService {
	private final RestClient restClient;

	public AuthenticationService(RestClient restClient) {
		this.restClient = restClient;
	}

	public User authenticate(String token) {
		return restClient.get()
				.uri("/users/@me")
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.body(User.class);
	}
}
