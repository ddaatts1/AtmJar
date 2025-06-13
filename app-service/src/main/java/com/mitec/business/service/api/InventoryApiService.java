package com.mitec.business.service.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitec.business.model.Accessory;
import com.mitec.business.model.Inventory;
import com.mitec.business.model.PartInventory;
import com.mitec.business.model.Series;
import com.mitec.business.repository.AccessoryRepository;
import com.mitec.business.repository.InventoryRepository;
import com.mitec.business.utils.PartInventoryTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryApiService {
	
	/*
	 * Api for App mobile
	*/
	
	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private AccessoryRepository accessoryRepository;
	
//	@Autowired
//	private PartInventoryRepository partInventoryRepository;
	
	public Map<Object, Object> getInventoriesByUser(String username) {
		Map<Object, Object> result = new HashMap<>();
		List<Map<Object, Object>> data = new ArrayList<>();
		
		List<Inventory> list = inventoryRepository.findByUsername(username);
		List<Accessory> listAcc = accessoryRepository.findAll();
		
		if (list != null && !list.isEmpty()) {
			data = list.stream().map(item -> {
				List<PartInventory> partAcc = new ArrayList<>();
				List<Map<Object, Object>> accessories = new ArrayList<>();
				partAcc = item.getPartInventories().stream().filter(pi -> pi.getQuantity() > 0 && pi.getType().equals(PartInventoryTypeEnum.ACCESSORY.getKey())).collect(Collectors.toList());
				
				Map<Object, Object> dataItem = new HashMap<>();
				dataItem.put("id", item.getId());
				dataItem.put("name", item.getName());
				dataItem.put("partInventories", getPartInventory(item.getPartInventories()));
				
				// Accessories
				for (PartInventory part : partAcc) {
					for (Accessory a : listAcc) {
						if (a.getId().equals(part.getPartId())) {
							a.setQuantity(part.getQuantity());
							a.setPartId(part.getId());
							accessories.add(jsonAccessory(a));
							break;
						}
					}
				}
				log.debug("partI" + partAcc.size());
				log.debug("access" + accessories.size());
				
				dataItem.put("accessories", accessories);
				
				return dataItem;
			}).collect(Collectors.toList());
		}
		
		result.put("data", data);
		return result;
	}
	
	private List<Map<Object, Object>> getPartInventory(List<PartInventory> list) {
		List<Map<Object, Object>> data = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			data = list.stream().filter(i -> i.getQuantity() > 0).map(item -> {
				Map<Object, Object> dataItem = new HashMap<>();
				dataItem.put("id", item.getId());
				dataItem.put("partId", item.getPartId());
				dataItem.put("name", item.getName());
				dataItem.put("type", item.getType());
				dataItem.put("typeStr", item.getTypeStr());
				dataItem.put("quantity", item.getQuantity());
				return dataItem;
			}).collect(Collectors.toList());
		}
		
		return data;
	}
	
	private Map<Object, Object> jsonAccessory(Accessory accessory) {
		Map<Object, Object> item = new HashMap<>();
		List<Map<Object, Object>> series = new ArrayList<>();
		item.put("id", accessory.getId());
		item.put("name", accessory.getName());
		item.put("errorDeviceId", accessory.getErrorDevice().getId());
		item.put("quantity", accessory.getQuantity());
		item.put("partId", accessory.getPartId());
		if (accessory.getSeries() != null && !accessory.getSeries().isEmpty()) {
			series = accessory.getSeries().stream().map(s -> jsonSeries(s)).collect(Collectors.toList());
		}
		item.put("series", series);
		
		return item;
	}
	
	private Map<Object, Object> jsonSeries(Series series) {
		Map<Object, Object> item = new HashMap<>();
		item.put("id", series.getId());
		item.put("name", series.getName());
		return item;
	}
	
//	public Map<Object, Object> getPartInventory(Long inventoryId) {
//		Map<Object, Object> result = new HashMap<>();
//		List<Map<Object, Object>> data = new ArrayList<>();
//		
//		List<PartInventory> list = partInventoryRepository.findByInventoryId(inventoryId);
//		if (list != null && !list.isEmpty()) {
//			data = list.stream().map(item -> {
//				Map<Object, Object> dataItem = new HashMap<>();
//				dataItem.put("id", item.getId());
//				dataItem.put("name", item.getName());
//				dataItem.put("type", item.getType());
//				dataItem.put("typeStr", item.getTypeStr());
//				dataItem.put("quantity", item.getQuantity());
//				return dataItem;
//			}).collect(Collectors.toList());
//		}
//		
//		result.put("data", data);
//		return result;
//	}
}
