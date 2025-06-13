package com.mitec.business.service.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.ErrorDto;
import com.mitec.business.model.Device;
import com.mitec.business.model.Error;
import com.mitec.business.repository.DeviceRepository;
import com.mitec.business.repository.ErrorRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.ClassMapper;

@Service
public class ErrorService {

	@Autowired
	private ErrorRepository errorRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ClassMapper classMapper;
	@Autowired
	private DeviceRepository deviceRepository;
	
	public Page<Error> listAllCommonError(int pageNumber, int size) {
		Specification<Error> spec = objectSpecification.searchError();
		return errorRepository.findAll(spec, PageRequest.of(pageNumber, size));
	}
	
	public Error saveError(String body) throws JSONException {
		Error error = new Error();
		JSONObject ob = new JSONObject(body);
		
		error.setName(ob.getString("name"));
		Long deviceId = Long.parseLong(ob.getString("device"));
		
		Device device = deviceRepository.getById(deviceId);
		error.setDevice(device);
		return errorRepository.save(error);
	}
	
	public void deleteError(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		errorRepository.deleteById(id);
	}
	
	public ErrorDto getError(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Error error = errorRepository.getById(id);
		return classMapper.convertToErrorDto(error);
	}
	
	public Error updateError(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Error error = errorRepository.getById(id);
		error.setName(ob.getString("name"));
		
		Long deviceId = Long.parseLong(ob.getString("device"));
		Device device = deviceRepository.getById(deviceId);
		error.setDevice(device);
		return errorRepository.save(error);
	}
}
