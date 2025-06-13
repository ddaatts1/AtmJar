package com.mitec.business.service.categories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.DeviceDto;
import com.mitec.business.model.Device;
import com.mitec.business.repository.DeviceRepository;
import com.mitec.business.specification.ObjectSpecification;

@Service
public class DeviceService {
	
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	
	public Page<Device> listAllDevice(int pageNumber, int size) {
		Specification<Device> spec = objectSpecification.searchDevice();
		Page<Device> page = deviceRepository.findAll(spec, PageRequest.of(pageNumber, size));
		List<Device> regions = page.getContent();
		List<Device> list = regions.stream().map(device -> {
			return device;
		}).collect(Collectors.toList());
		
		return new PageImpl<>(list, PageRequest.of(pageNumber, size), page.getTotalElements());
	}
	
	public List<DeviceDto> listDeviceCommonError() {
		return deviceRepository.findAllDto();
	}
	
	public Device saveDevice(String body) throws JSONException {
		Device device = new Device();
		JSONObject ob = new JSONObject(body);
		device.setName(ob.getString("name"));
		
		return deviceRepository.save(device);
	}
	
	public void deleteDevice(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		deviceRepository.deleteById(id);
	}
	
	public DeviceDto getDevice(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		return deviceRepository.getDtoById(id);
	}
	
	public Device updateDevice(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Device device = deviceRepository.getById(id);
		device.setName(ob.getString("name"));
		
		return deviceRepository.save(device);
	}
}
