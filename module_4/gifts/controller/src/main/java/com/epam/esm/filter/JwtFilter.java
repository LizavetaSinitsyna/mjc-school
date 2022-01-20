package com.epam.esm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.epam.esm.security.JwtProvider;
import com.epam.esm.service.UserService;

@Component
public class JwtFilter extends GenericFilterBean {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String TOKEN_HEADER_PREFIX = "Bearer ";
	public static final int TOKEN_HEADER_FIRST_CHAR_INDEX = 7;

	private final JwtProvider jwtProvider;
	private final UserService userService;

	@Autowired
	public JwtFilter(JwtProvider jwtProvider, UserService userService) {
		this.jwtProvider = jwtProvider;
		this.userService = userService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = obtainTokenFromRequest((HttpServletRequest) request);
		if (token != null && jwtProvider.validateToken(token)) {
			String userLogin = jwtProvider.obtainLoginFromToken(token);
			UserDetails userDetails = userService.loadUserByUsername(userLogin);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		chain.doFilter(request, response);
	}

	private String obtainTokenFromRequest(HttpServletRequest request) {
		String token = null;
		String bearer = request.getHeader(AUTHORIZATION_HEADER);
		if (bearer != null && bearer.startsWith(TOKEN_HEADER_PREFIX)) {
			token = bearer.substring(TOKEN_HEADER_FIRST_CHAR_INDEX);
		}
		return token;
	}
}
