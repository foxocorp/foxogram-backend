package app.foxochat.service.impl;

import app.foxochat.config.JwtConfig;
import app.foxochat.constant.TokenConstant;
import app.foxochat.model.User;
import app.foxochat.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

	private final JwtConfig jwtConfig;

	public JwtServiceImpl(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	@Override
	public String generate(User user) {
		long now = System.currentTimeMillis();
		Date expirationDate = new Date(now + TokenConstant.LIFETIME);

		return Jwts.builder()
				.id(String.valueOf(user.getId()))
				.expiration(expirationDate)
				.signWith(getSigningKey())
				.subject(String.valueOf(user.getTokenVersion()))
				.compact();
	}

	@Override
	public SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
	}
}
