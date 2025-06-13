package com.mitec.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mitec.business.dto.ResultApi;
import com.mitec.business.service.SendMailService;
import com.mitec.business.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class LoginController {
	
	@Autowired
	private UserService appUserService;
	
	@Autowired
	private SendMailService sendMailService;
	
	@PostMapping(value = "/changePwd")
	public ResponseEntity<Object> changePassword(@RequestBody String body) throws JsonProcessingException {
		log.debug("Processing changePassword()....");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-type", "application/json; charset=UTF-8");
		Map<String, Object> result = new HashMap<>();
		
		ResultApi changePwdResult = appUserService.changePassword(body);
		result.put("message", changePwdResult.getMessage());
		if(changePwdResult.isSuccess()) {
			return new ResponseEntity<>(result, headers, HttpStatus.OK);
		}
		return new ResponseEntity<>(result, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping("/forgotPwd")
	public ResponseEntity<Object> forgotPwd(@RequestBody String body) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-type", "application/json; charset=UTF-8");
		try {
			ResultApi resultApi = sendMailService.forgotPwd(body).join();
			if (resultApi.isSuccess()) {
				return new ResponseEntity<>(resultApi, headers, HttpStatus.OK);
			}else {
				return new ResponseEntity<>(resultApi, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch (Exception e) {
			log.debug("=============>" + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
