package com.mitec.business.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.CloneStatisticalEvent;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Kpsc;
import com.mitec.business.model.Statistical;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.ReplacementAccessoryRepository;
import com.mitec.business.repository.StatisticalRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CloneStatisticalEventListener implements ApplicationListener<CloneStatisticalEvent> {

	@Autowired
	private StatisticalRepository statisticalRepository;
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private ReplacementAccessoryRepository replacementAccessoryRepository;
	
	@Override
	public void onApplicationEvent(CloneStatisticalEvent event) {
		log.debug("Processing cloneToStatistical service()....");
		Statistical statistical = statisticalRepository.findByJobId(event.getJob().getId()).orElse(new Statistical());
		statistical.setJobId(event.getJob().getId());
		statistical.setMaintenance(event.getJob().isMaintenance());
		statistical.setJobCompleteTime(event.getJob().getCompleteTime());
		statistical.setUsername(event.getJob().getUser().getUsername());
		statistical.setStatus(event.getJob().getStatus());
		
		ATM atm = event.getJob().getAtm();
		if (atm.getDepartment() != null) {
			statistical.setDepartmentId(atm.getDepartment().getId());
		}
		if (atm.getRegion() != null) {
			statistical.setRegionId(atm.getRegion().getId());
		}
		statistical.setSerialNumber(atm.getSerialNumber());
		
		Contract contract = contractRepository.findCurrentContract(atm.getSerialNumber()).orElse(null);
		if(contract != null) {
			statistical.setContractId(contract.getId());
		}
		if(replacementAccessoryRepository.getSumQuantityByJobId(event.getJob().getId())==null) {
			statistical.setQuantity(0);
		}else {
			statistical.setQuantity(replacementAccessoryRepository.getSumQuantityByJobId(event.getJob().getId()));
			
		}
		
		try {
			if (event.getJob().getKpscs() != null && !event.getJob().getKpscs().isEmpty()) {
				for (Kpsc k : event.getJob().getKpscs()) {
					if (k.getJobPerform() == null) continue;
					
					if (k.getJobPerform().getId().equals(5L)) {
						statistical.setIsRemoteService(true);
						break;
					}
				}
			}
		}catch (Exception e) {
			log.debug("Error: Clone statistical: " + e.getMessage());
		}
	
		statisticalRepository.save(statistical);
	}

}
