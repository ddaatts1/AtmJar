package com.mitec.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitec.business.model.Statistical;
import com.mitec.business.repository.StatisticalRepository;

@RestController
public class UndergroundApiController {

	@Autowired
	private StatisticalRepository statisticalRepository;
	
	private void updateIsRemote() {
		List<Statistical> statisticals = statisticalRepository.findAll();
		statisticals.stream().forEach(item -> {

			Statistical stat = statisticalRepository.getById(item.getId());
			
			stat.setIsRemoteService(statisticalRepository.isRemoteService(item.getId()));
			
			statisticalRepository.save(stat);
		});
	}
	
	@PostMapping("/fake/update-statistical")
	@ResponseStatus(code = HttpStatus.OK)
	public String updateStatistical() {
		updateIsRemote();
		return "Success";
	}
}
