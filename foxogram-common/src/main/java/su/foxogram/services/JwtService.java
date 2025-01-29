package su.foxogram.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.configs.JwtConfig;
import su.foxogram.constants.TokenConstants;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
	private final JwtConfig jwtConfig;

	@Autowired
	public JwtService(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	public String generate(long id, String passwordHash) {
		long now = System.currentTimeMillis();
		Date expirationDate = new Date(now + TokenConstants.LIFETIME);

		return Jwts.builder()
				.setId(String.valueOf(id))
				.setSubject(passwordHash)
				.setExpiration(expirationDate)
				.signWith(getSigningKey())
				.compact();
	}

	public Key getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
	}
}
