package com.mitec.business.service.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mitec.business.model.Department;
import com.mitec.business.repository.DepartmentRepository;
import com.mitec.business.repository.RegionRepository;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private RegionRepository regionRepository;
	
	private static final String ID = "id";
	private static final String REGION = "region";
	private static final String NAME = "name";

	public Page<Department> getDepartments(int pageNumber, int pageSize) {
		return departmentRepository.findAll(PageRequest.of(pageNumber, pageSize));
	}
	
	public Department saveDepartment(String body) throws JSONException {
		Department department = new Department();
		JSONObject ob = new JSONObject(body);
		department.setName(ob.getString(NAME));
		if (!ob.isNull(REGION)) {
			department.setRegion(regionRepository.getById(ob.getLong("region")));
		}
		return departmentRepository.save(department);
	}
	public List<Department> listDepartment() {
		return departmentRepository.findAll(Sort.by(Sort.Direction.DESC, ID));
	}
	
	public void deleteDepartment(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString(ID));
		departmentRepository.deleteById(id);
	}
	public Department getDepartment(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString(ID));
		return departmentRepository.getById(id);
	}
	public Department updateDepartment(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString(ID));
		Department department = departmentRepository.getById(id);
		department.setName(ob.getString(NAME));
		if (!ob.isNull(REGION)) {
			department.setRegion(regionRepository.getById(ob.getLong(REGION)));
		}
		return departmentRepository.save(department);
	}
}
