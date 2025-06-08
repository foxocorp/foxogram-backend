package su.foxochat.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import su.foxochat.config.JwtConfig;
import su.foxochat.constant.TokenConstant;
import su.foxochat.model.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements su.foxochat.service.JwtService {

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
				.signWith(getSigningKey(user.getTokenVersion()))
				.compact();
	}

	@Override
	public SecretKey getSigningKey(int tokenVersion) {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret() + tokenVersion));
	}
}
