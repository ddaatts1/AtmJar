package com.mitec.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.mitec.business.service.categories.AtmService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {

	@Autowired
	private AtmService atmService;

	/*
	 * Chạy schedule đầu ngày, quét toàn bộ ATM,
	 * Đổi trạng thái ATM khi hợp đồng hết hạn
	*/
	@Scheduled(cron = "0 0 0 * * *")
	public void changeContractStatus() {
		log.debug("=====> Check atm status ()");
		atmService.atmSchedule();
	}


	
}
