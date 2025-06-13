package com.mitec.business.service.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.ErrorDeviceDto;
import com.mitec.business.event.AddNewPartEvent;
import com.mitec.business.event.UpdatePartEvent;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.repository.ErrorDeviceRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.PartInventoryTypeEnum;

@Service
public class ErrorDeviceService {

	@Autowired
	private ErrorDeviceRepository errorDeviceRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	public ErrorDevice saveErrorDevice(String body) throws JSONException {
		ErrorDevice errorDevice = new ErrorDevice();
		JSONObject ob = new JSONObject(body);
		errorDevice.setName(ob.getString("name"));
		
		errorDevice = errorDeviceRepository.save(errorDevice);

		applicationEventPublisher.publishEvent(new AddNewPartEvent(this, errorDevice.getId(), errorDevice.getName(), PartInventoryTypeEnum.DIVICE.getKey()));
		
		return errorDevice;
	}
	
	public List<ErrorDevice> listErrorDevice() {
		return errorDeviceRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
	
	public Page<ErrorDevice> listAllErrorDevice(int pageNumber, int size) {
		Specification<ErrorDevice> spec = objectSpecification.searchErrorDevice();
		return errorDeviceRepository.findAll(spec, PageRequest.of(pageNumber, size));
	}
	
	public void deleteErrorDevice(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		errorDeviceRepository.deleteById(id);
	}
	
	public ErrorDeviceDto getErrorDevice(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		return errorDeviceRepository.getDtoById(id);
	}
	
	public ErrorDevice updateErrorDevice(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		ErrorDevice errorDevice = errorDeviceRepository.getById(id);
		errorDevice.setName(ob.getString("name"));
		
		errorDevice = errorDeviceRepository.save(errorDevice);
	
		applicationEventPublisher.publishEvent(new UpdatePartEvent(this, errorDevice.getId(), errorDevice.getName(), PartInventoryTypeEnum.DIVICE.getKey()));
		
		return errorDevice;
	}
}
