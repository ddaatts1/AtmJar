package com.mitec.business.service.categories;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitec.business.model.Contract;
import com.mitec.business.model.Period;
import com.mitec.business.repository.PeriodRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeriodService {

	@Autowired
	private PeriodRepository periodRepository;
	
	public void getPeriod(Contract contract) {
		log.debug("============> getPeriod()....");
		if (contract.getStartTime() != null) {
			LocalDateTime virtualTime = contract.getStartTime();
			//index
			int i = 1;
			
			if (contract.getPeriods() != null && !contract.getPeriods().isEmpty()) {
				periodRepository.deleteAll(contract.getPeriods());
			}
			
			while(virtualTime.isBefore(contract.getEndTime())) {
				Period period = new Period();
				period.setIndex(i);
				period.setContract(contract);
				period.setStartTime(virtualTime);
				
				if (virtualTime.plusMonths(contract.getMaintenanceCycle()).isAfter(contract.getEndTime())) {
					period.setEndTime(contract.getEndTime());
				}else {
					period.setEndTime(virtualTime.plusMonths(contract.getMaintenanceCycle()));
				}
				
				period = periodRepository.save(period);
				
				log.debug("==============>" + period);
				
				virtualTime = virtualTime.plusMonths(contract.getMaintenanceCycle());
				i++;
			}
		}
	}
}
