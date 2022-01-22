package com.epam.esm.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epam.esm.security.JwtProvider;
import com.epam.esm.service.UserService;

@Component
public class JwtFilter extends OncePerRequestFilter {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String TOKEN_HEADER_PREFIX = "Bearer ";
	public static final int TOKEN_HEADER_FIRST_CHAR_INDEX = 7;
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	private final Map<String, String> ignoredRequests;

	private final JwtProvider jwtProvider;
	private final UserService userService;

	@Autowired
	public JwtFilter(JwtProvider jwtProvider, UserService userService) {
		this.jwtProvider = jwtProvider;
		this.userService = userService;

		ignoredRequests = new HashMap<>();
		ignoredRequests.put("GET", "/api/v1/certificates/**");
		ignoredRequests.put("POST", "/api/v1/users/**");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = obtainTokenFromRequest((HttpServletRequest) request);
		if (token != null && jwtProvider.validateToken(token)) {
			String userLogin = jwtProvider.obtainLoginFromToken(token);
			UserDetails userDetails = userService.loadUserByUsername(userLogin);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(request, response);
	}

	private String obtainTokenFromRequest(HttpServletRequest request) {
		String token = null;
		String bearer = request.getHeader(AUTHORIZATION_HEADER);
		if (bearer != null && bearer.startsWith(TOKEN_HEADER_PREFIX)) {
			token = bearer.substring(TOKEN_HEADER_FIRST_CHAR_INDEX);
		}
		return token;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		for (Map.Entry<String, String> ignoredRequest : ignoredRequests.entrySet()) {
			if (request.getMethod().equals(ignoredRequest.getKey())
					&& pathMatcher.match(ignoredRequest.getValue(), request.getServletPath())) {
				return true;
			}
		}
		return false;
	}
}
