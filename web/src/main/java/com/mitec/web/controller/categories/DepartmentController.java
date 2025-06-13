package com.mitec.web.controller.categories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mitec.business.model.Department;
import com.mitec.business.model.Region;
import com.mitec.business.service.categories.DepartmentService;
import com.mitec.business.service.categories.RegionService;
import com.mitec.web.controller.BaseController;

@Controller
public class DepartmentController extends BaseController {

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RegionService regionService;
	
	@GetMapping("/departments")
	public ModelAndView departments(@RequestParam(name = "page") Optional<Integer> pageNumber, @RequestParam(name = "size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/departments");
		List<Region> regions = regionService.getRegions();
		Page<Department> pages = departmentService.getDepartments(pageNumber.orElse(appProperties.getDefaultPage()) - 1, pageSize.orElse(appProperties.getDefaultPageSize()));
		
		model.addObject("regions", regions);
		model.addObject("pages", pages);
		return model;
	}
	
	@ResponseBody
	@PostMapping("/department/save")
	public ResponseEntity<Object> saveDepartment(@RequestBody String body) throws JSONException {
		try {
			departmentService.saveDepartment(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@ResponseBody
	@PostMapping("/department/delete")
	public ResponseEntity<Object> deleteDepartment(@RequestBody String body) throws JSONException {
		try {
			departmentService.deleteDepartment(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping("/department/edit")
	public ResponseEntity<Object> getDepartment(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		Department department = departmentService.getDepartment(body);
		Map<String, Object> data = new HashMap<>();
		data.put("id", department.getId());
		data.put("name", department.getName());
		if (department.getRegion() != null) {
			data.put("regionId", department.getRegion().getId());
		}else {
			data.put("regionId", null);
		}
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/department/update")
	public ResponseEntity<Object> updateDepartment(@RequestBody String body) {
		try {
			departmentService.updateDepartment(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
}
