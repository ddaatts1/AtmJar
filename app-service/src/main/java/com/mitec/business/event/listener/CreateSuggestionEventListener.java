package com.mitec.business.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.CreateSuggestionEvent;
import com.mitec.business.model.Suggestion;
import com.mitec.business.repository.SuggestionRepository;

@Component
public class CreateSuggestionEventListener implements ApplicationListener<CreateSuggestionEvent> {

	@Autowired
	private SuggestionRepository suggestionRepository;
	
	@Override
	public void onApplicationEvent(CreateSuggestionEvent event) {
		String name = event.getName();
		String phone = event.getPhone();
		String department = event.getDepartment();
		String address= event.getAddress();
		
		Suggestion item = suggestionRepository.findByNameAndPhone(name, phone).orElse(new Suggestion());
		
		item.setName(name);
		item.setPhone(phone);
		item.setDepartment(department);
		item.setAddress(address);
		
		suggestionRepository.save(item);
	}

}
