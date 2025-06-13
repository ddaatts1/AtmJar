package com.mitec.business.event.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.CloneHistoryEvent;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Contract;
import com.mitec.business.model.History;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.HistoryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CloneHistoryEventListener implements ApplicationListener<CloneHistoryEvent> {

	@Autowired
	private ContractRepository contractRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	
	@Override
	public void onApplicationEvent(CloneHistoryEvent event) {
		List<ATM> atms = event.getAtms();
		
		log.debug("atms size: " + atms.size());
		
		atms.stream().forEach(item -> {
			History history = new History();
			history.setSerialNumber(item.getSerialNumber());
			history.setAddress(item.getAddress());
			
			Contract contract = contractRepository.findCurrentContract(item.getSerialNumber()).orElse(null);
			if (contract != null) {
				history.setContractId(contract.getId());
			}
			if (item.getSeries() != null) {
				history.setSeriesId(item.getSeries().getId());
			}
			if (item.getRegion() != null) {
				history.setRegionId(item.getRegion().getId());
			}
			if (item.getDepartment() != null) {
				history.setDepartmentId(item.getDepartment().getId());
			}
			history.setStatus(item.getStatus());
			
			historyRepository.save(history);
		});
	}

}
