package com.mitec.business.event.listener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mitec.business.event.UpdateInventoryCheckOutEvent;
import com.mitec.business.model.PartInventory;
import com.mitec.business.model.Tracking;
import com.mitec.business.model.TrackingDetail;
import com.mitec.business.repository.PartInventoryRepository;
import com.mitec.business.repository.TrackingDetailRepository;
import com.mitec.business.repository.TrackingRepository;
import com.mitec.business.service.InventoryService;
import com.mitec.business.utils.TrackingStatusEnum;
import com.mitec.business.utils.TrackingTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UpdateInventoryCheckOutEventListener implements ApplicationListener<UpdateInventoryCheckOutEvent> {
	
	/*
	 * tạo phiếu xuất kho khi check có thay thế linh kiện
	*/
	
	@Autowired
	private TrackingRepository trackingRepository;
	@Autowired
	private TrackingDetailRepository trackingDetailRepository;
	@Autowired
	private PartInventoryRepository partInventoryRepository;
	
	@Override
	@Transactional
	public void onApplicationEvent(UpdateInventoryCheckOutEvent event) {
		log.debug("Update part inventory event()....");
		JSONArray listJsonAcc = event.getListJsonAcc();
		String username = event.getUsername();
		Set<Long> setInventoryId = new HashSet<>();
		List<IventoryEventData> listData = new ArrayList<>();
		
		if (listJsonAcc != null && listJsonAcc.length() > 0) {
			for(int i = 0; i < listJsonAcc.length(); i++) {
				try {
					JSONObject item = listJsonAcc.getJSONObject(i);
					setInventoryId.add(item.getLong("inventoryId"));
					listData.add(new IventoryEventData(item.getLong("inventoryId"), item.getLong("partId"), 
							item.getLong("quantity"), item.getLong("accessoryId")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		String tradingCode = InventoryService.genTradingCode();
		
		if (!setInventoryId.isEmpty()) {
			for (Long item : setInventoryId) {
				Tracking tracking = new Tracking();
				tracking.setSender(username);
				tracking.setType(TrackingTypeEnum.XUAT_KHO.getKey());
				tracking.setSendedInventory(item);
				tracking.setSendDate(event.getTime());
				tracking.setStatus(TrackingStatusEnum.DANG_CHO.getKey());
				tracking.setTradingCode(tradingCode);
				
				tracking = trackingRepository.save(tracking);
				
				for (IventoryEventData dataItem : listData) {
					if (dataItem.getInventoryId().equals(item)) {
						TrackingDetail trackingDetail = new TrackingDetail();
						PartInventory partInventory = partInventoryRepository.getById(dataItem.getPartInvetoryId());
						trackingDetail.setPartId(dataItem.getPartId());
						trackingDetail.setPartInventoryId(dataItem.getPartInvetoryId());
						trackingDetail.setPartName(partInventory.getName());
						trackingDetail.setType(partInventory.getType());
						trackingDetail.setTracking(tracking);
						trackingDetail.setQuantity(dataItem.getQuantity());
						
						trackingDetailRepository.save(trackingDetail);
					}
				}
			}
			
		}
	}
}

@Getter
@Setter
@AllArgsConstructor
class IventoryEventData {
	private Long inventoryId;
	private Long partInvetoryId;
	private Long quantity;
	private Long partId;
}
