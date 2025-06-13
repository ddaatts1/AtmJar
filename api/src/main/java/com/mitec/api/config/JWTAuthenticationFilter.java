package com.mitec.api.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitec.api.constant.SecurityConstants;
import com.mitec.business.model.User;
import com.mitec.business.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		
		setFilterProcessesUrl("/login");
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			User creds = new ObjectMapper().readValue(req.getInputStream(), User.class);
			String userAndDeviceId = creds.getUsername() + ":" + creds.getDeviceId();
			
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAndDeviceId,
					creds.getPassword(), new ArrayList<>()));
		} catch (IOException e) {
			log.debug(e.toString());
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException {
		String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
		
		String token = JWT.create().withSubject(username)
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
		
		User user = userService.getUserByUsername(username);
		
		Map<String, Object> data = new HashMap<>();
		data.put("token", SecurityConstants.TOKEN_PREFIX + token);
		data.put("username", user.getUsername());
		data.put("fullName", user.getFullName());
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().print(new ObjectMapper().writeValueAsString(data));
		res.getWriter().flush();
		
	}
}