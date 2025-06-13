package com.mitec.api.service;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mitec.api.constant.SecurityConstants;

@Service
public class AuthorizationService {
	
	public static String getUser(String token) {
		return JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
	}
}
