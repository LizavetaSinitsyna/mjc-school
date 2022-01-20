package com.epam.esm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.epam.esm.filter.ExceptionHandlerFilter;
import com.epam.esm.filter.JwtFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final JwtFilter jwtFilter;
	private final ExceptionHandlerFilter exceptionHandlerFilter;

	@Autowired
	public SecurityConfig(JwtFilter jwtFilter, ExceptionHandlerFilter exceptionHandlerFilter) {
		this.jwtFilter = jwtFilter;
		this.exceptionHandlerFilter = exceptionHandlerFilter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable().csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.anonymous().disable()/*-.and().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/v1/certificates/*").hasRole("ADMIN")
				.antMatchers(HttpMethod.PATCH).hasRole("ADMIN")
				.antMatchers(HttpMethod.PUT).hasRole("ADMIN")
				.antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
				.antMatchers("/api/v1/orders").hasRole("ADMIN")
				.antMatchers("/api/v1/orders").hasRole("ADMIN")
				.antMatchers("/api/v1/users/register", "/api/v1/users/auth").permitAll()*/
				.addFilterBefore(exceptionHandlerFilter, SecurityContextPersistenceFilter.class)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
