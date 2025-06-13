package com.mitec.business.event.listener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.CheckOutEvent;
import com.mitec.business.model.Contract;
import com.mitec.business.model.CustomerEmail;
import com.mitec.business.model.Job;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.CustomerEmailRepository;
import com.mitec.business.service.SendMailService;
import com.mitec.business.service.ThymeleafService;

@Component
public class CheckOutEventListener implements ApplicationListener<CheckOutEvent> {
	
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private ThymeleafService thymeleafService;
	@Autowired
	private CustomerEmailRepository customerEmailRepository;
	@Autowired
	private SendMailService sendMailService;

	@Override
	public void onApplicationEvent(CheckOutEvent event) {
		Job job = event.getJob();
		// Lấy danh sách hợp đồng
		List<Contract> contracts = contractRepository.getBySerialNumber(job.getAtm().getSerialNumber());
		CustomerEmail internalEmail = customerEmailRepository.getInternalEmail().orElse(new CustomerEmail());

		List<Map<String, Object>> customerEmails = new ArrayList<>();
		
		if (contracts != null && !contracts.isEmpty()) {
			contracts.stream().filter(item -> item.getEndTime() != null && item.getStartTime() != null)
				.forEach(item -> {
					boolean isExpired = true;
					if (LocalDateTime.now().isAfter(item.getEndTime())) {
						isExpired = false;
					}
					if (item.getCustomerEmails() != null && !item.getCustomerEmails().isEmpty()) {
						for (CustomerEmail emailItem : item.getCustomerEmails()) {
							Map<String, Object> email = new HashMap<>();
							email.put("email", emailItem.getEmail());
							email.put("isExpired", isExpired);
							
							customerEmails.add(email);
						}
					}
			});
		}
		
		Map<String, String> contentMail = new HashMap<>();
		contentMail.put("subject", "Báo cáo");
		if (StringUtils.isNotBlank(internalEmail.getEmail())) {
			contentMail.put("body", thymeleafService.getCheckOutContent(event.getJob(), false, workDetail(job.isMaintenance(), job.isKpsc(), false)));
			try {
				sendMailService.sendMail(internalEmail.getEmail(), contentMail);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!customerEmails.isEmpty()) {
			for (Map<String, Object> email : customerEmails) {
				contentMail.put("body", thymeleafService.getCheckOutContent(event.getJob(), true, workDetail(job.isMaintenance(), job.isKpsc(), (boolean) email.get("isExpired"))));
				try {
					sendMailService.sendMail((String) email.get("email"), contentMail);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String workDetail(boolean isMaintenance, boolean isKpsc, boolean isExpired) {
    	StringBuilder sb = new StringBuilder();
    	if (isExpired) {
    		sb.append("Hỗ trợ khách hàng khắc phục sự cố");
    	}else {
    		if (isMaintenance) {
        		sb.append("Bảo trì định kỳ ");
        	}
        	if (isMaintenance && isKpsc) {
        		sb.append("và ");
        	}
        	if (isKpsc) {
        		sb.append("Khắc phục sự cố");
        	}
    	}
    	return sb.toString();
    }
}
