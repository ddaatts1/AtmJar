package com.mitec.business;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.mitec.business.model.EmailConfig;
import com.mitec.business.repository.EmailConfigRepository;

@ComponentScan(basePackages = {"com.mitec.business"})

@SpringBootApplication
public class AppServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppServiceApplication.class, args);
	}
	
	@Autowired
	private EmailConfigRepository emailConfigRepository;
	
	@Bean
	public JavaMailSender javaMailSender() {
		EmailConfig email = emailConfigRepository.findAll().get(0);
		
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost("smtp.gmail.com");
		mailSenderImpl.setPort(587);
	    
		mailSenderImpl.setUsername(email.getUsername());
		mailSenderImpl.setPassword(email.getPassword());
		
		Properties props = mailSenderImpl.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	    
	    return mailSenderImpl;
	}



}
