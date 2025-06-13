package com.mitec.business.event.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.CreatePeriodEvent;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Period;
import com.mitec.business.repository.PeriodRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CreatePeriodEventListener implements ApplicationListener<CreatePeriodEvent> {
	
	@Autowired
	private PeriodRepository periodRepository;
	
	@Override
	public void onApplicationEvent(CreatePeriodEvent event) {
		log.debug("============> create Period()....");
		Contract contract = event.getContract();
		if (contract.getStartTime() != null && contract.getEndTime() != null) {
			LocalDateTime virtualTime = contract.getStartTime();
			// index
			int i = 1;

			if (contract.getPeriods() != null && !contract.getPeriods().isEmpty()) {
				contract.getPeriods().stream().forEach(item -> periodRepository.delete(item));
			}

			while (virtualTime.isBefore(contract.getEndTime())) {
				Period period = new Period();
				period.setIndex(i);
				period.setContract(contract);
				period.setStartTime(virtualTime);

				if (virtualTime.plusMonths(contract.getMaintenanceCycle()).isAfter(contract.getEndTime())) {
					period.setEndTime(contract.getEndTime());
				} else {
					period.setEndTime(virtualTime.plusMonths(contract.getMaintenanceCycle()));
				}
				periodRepository.save(period);
				
				virtualTime = virtualTime.plusMonths(contract.getMaintenanceCycle());
				i++;
			}
		}
	}

}
