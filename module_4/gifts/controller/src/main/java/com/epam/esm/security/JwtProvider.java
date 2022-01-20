package com.epam.esm.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.epam.esm.exception.InvalidJwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration.seconds}")
	private long jwtExpiration;

	public String generateToken(String login) {
		Date date = Date
				.from(LocalDateTime.now().plusSeconds(jwtExpiration).atZone(ZoneId.systemDefault()).toInstant());
		return Jwts.builder().setSubject(login).setExpiration(date).signWith(SignatureAlgorithm.HS256, jwtSecret)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new InvalidJwtException(e.getMessage());
		}
	}
	
	public String obtainLoginFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}
}
