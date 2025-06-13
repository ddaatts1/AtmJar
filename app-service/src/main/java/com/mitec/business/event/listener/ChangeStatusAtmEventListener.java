package com.mitec.business.event.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.ChangeStatusAtmEvent;
import com.mitec.business.model.ATM;
import com.mitec.business.repository.ATMRepository;
import com.mitec.business.utils.AtmStatusEnum;

@Component
public class ChangeStatusAtmEventListener implements ApplicationListener<ChangeStatusAtmEvent>{
	
	@Autowired
	private ATMRepository atmRepository;

	@Override
	public void onApplicationEvent(ChangeStatusAtmEvent event) {
		 List<ATM> list = event.getList();
		 list.stream().forEach(item -> {
			 item.setStatus(AtmStatusEnum.PROVIDING.getKey());
			 atmRepository.save(item);
		 });
	}

}
