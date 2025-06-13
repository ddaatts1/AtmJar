package com.mitec.business.event.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.AddNewPartEvent;
import com.mitec.business.model.Inventory;
import com.mitec.business.model.PartInventory;
import com.mitec.business.repository.InventoryRepository;
import com.mitec.business.repository.PartInventoryRepository;

@Component
public class AddNewPartListener implements ApplicationListener<AddNewPartEvent> {

	@Autowired
	private PartInventoryRepository repository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Override
	public void onApplicationEvent(AddNewPartEvent event) {
		List<Inventory> inventories = inventoryRepository.findAll();
		
		if (!inventories.isEmpty()) {
			for (Inventory inven : inventories) {
				PartInventory part = new PartInventory();
				
				part.setPartId(event.getPartId());
				part.setName(event.getPartName());
				part.setType(event.getType());
				part.setInventory(inven);
				
				repository.save(part);
			}
		}
	}

}
