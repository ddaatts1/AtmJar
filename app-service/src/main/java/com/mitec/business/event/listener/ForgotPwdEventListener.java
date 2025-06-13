package com.mitec.business.event.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.ForgotPwdEvent;
import com.mitec.business.service.SendMailService;

@Component
public class ForgotPwdEventListener implements ApplicationListener<ForgotPwdEvent> {

	@Autowired
	private SendMailService sendMailService;
	
	@Override
	public void onApplicationEvent(ForgotPwdEvent event) {
		Map<String, String> contentMail = resetPwdBody(event.getNewPwd());
		sendMailService.sendMail(event.getEmail(), contentMail);
	}
	
	private Map<String, String> resetPwdBody(String newPwd) {
		Map<String, String> mailContent = new HashMap<>();
		StringBuilder subject = new StringBuilder();
		StringBuilder body = new StringBuilder();
		
		subject.append("Mật khẩu của bạn đã được reset!");
		
		body.append("<p>Mật khẩu của bạn đã được đổi thành: <span style='font-weight: bold;'>"+ newPwd +"<span></p>");
		
		mailContent.put("subject", subject.toString());
		mailContent.put("body", body.toString());
		return mailContent;
	}
}
