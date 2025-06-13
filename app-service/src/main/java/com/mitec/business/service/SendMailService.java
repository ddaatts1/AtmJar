package com.mitec.business.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.ResultApi;
import com.mitec.business.event.ForgotPwdEvent;
import com.mitec.business.model.User;
import com.mitec.business.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendMailService {

	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Async
	public CompletableFuture<ResultApi> forgotPwd(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		ResultApi resultApi = new ResultApi();
		
		String username = ob.getString("username");
		User user = userRepository.findByUsername(username);
		
		String email = ob.getString("email");
		if (StringUtils.isNotBlank(email) && email.trim().equals(user.getEmail())) {
			String newPwd = genarateNewPwd();
			user.setPassword(bCryptPasswordEncoder.encode(newPwd));
			
			userRepository.save(user);
			
			applicationEventPublisher.publishEvent(new ForgotPwdEvent(this, email, newPwd));
			
			resultApi.setSuccess(true);
			resultApi.setMessage("Mật khẩu mới sẽ được gửi về email của bạn, vui lòng kiểm tra!");
		}else {
			resultApi.setSuccess(false);
			resultApi.setMessage("Email bạn nhập không đúng với email mà bạn đăng ký.");
		}
		
		return CompletableFuture.completedFuture(resultApi);
	}
	
	private String genarateNewPwd() {
		int length = 10;
	    boolean useLetters = true;
	    boolean useNumbers = false;
	    return RandomStringUtils.random(length, useLetters, useNumbers);
	}
	
	public boolean sendMail(String email, Map<String, String> contentMail) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			
			MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeHelper.setTo(email);
			
			mimeHelper.setSubject(contentMail.get("subject"));
			
			mimeHelper.setText(contentMail.get("body"), true);
			
			javaMailSender.send(mimeMessage);
	        return true;
		}catch (Exception e) {
			log.debug("SendMail err: " + e.getMessage());
			return false;
		}
	}
}
