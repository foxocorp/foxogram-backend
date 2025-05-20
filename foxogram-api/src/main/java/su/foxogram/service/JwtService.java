package su.foxogram.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.config.JwtConfig;
import su.foxogram.constant.TokenConstant;

import javax.crypto.SecretKey;
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
		Date expirationDate = new Date(now + TokenConstant.LIFETIME);

		return Jwts.builder()
				.id(String.valueOf(id))
				.subject(passwordHash)
				.expiration(expirationDate)
				.signWith(getSigningKey())
				.compact();
	}

	public SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
	}
}
