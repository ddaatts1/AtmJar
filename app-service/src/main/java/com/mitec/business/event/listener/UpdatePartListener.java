package com.mitec.business.event.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.UpdatePartEvent;
import com.mitec.business.model.Inventory;
import com.mitec.business.model.PartInventory;
import com.mitec.business.repository.InventoryRepository;
import com.mitec.business.repository.PartInventoryRepository;

@Component
public class UpdatePartListener implements ApplicationListener<UpdatePartEvent> {

	@Autowired
	private PartInventoryRepository partInventoryRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;

	@Override
	public void onApplicationEvent(UpdatePartEvent event) {
		Long partId = event.getPartId();
		
		List<Inventory> inventories = inventoryRepository.findAll();
		
		if (!inventories.isEmpty()) {
			for (Inventory item : inventories) {
				PartInventory part = partInventoryRepository.getByPart(item.getId(), partId, event.getPartType()).orElse(new PartInventory());
				
				if (part.getId() == null) {
					part.setInventory(item);
					part.setPartId(partId);
				}
				
				part.setName(event.getPartName());
				part.setType(event.getPartType());
				
				partInventoryRepository.save(part);
			}
		}
	}

}