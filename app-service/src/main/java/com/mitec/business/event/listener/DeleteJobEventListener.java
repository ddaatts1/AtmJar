package com.mitec.business.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.DeleteJobEvent;
import com.mitec.business.repository.StatisticalRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeleteJobEventListener implements ApplicationListener<DeleteJobEvent>{

	@Autowired
	private StatisticalRepository statisticalRepository;
	
	@Override
	public void onApplicationEvent(DeleteJobEvent event) {
		log.debug("Processing eventDeleteJobHandle()....");
		if (event.getJobs() != null) {
			event.getJobs().stream().forEach(item -> statisticalRepository.deleteByJobId(item.getId()));
		}
		if (event.getJobId() != null) {
			statisticalRepository.deleteByJobId(event.getJobId());
		}
	}

}
