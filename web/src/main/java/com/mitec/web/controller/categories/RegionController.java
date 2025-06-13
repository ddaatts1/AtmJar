package com.mitec.web.controller.categories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.mitec.business.model.Region;
import com.mitec.business.service.categories.RegionService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RegionController extends BaseController {

	@Autowired
	private RegionService regionService;
	
	@GetMapping("/regions")
	public ModelAndView regions(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/regions");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<Region> pages = regionService.listAllRegion(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		return model;
	}
	
	@ResponseBody
	@PostMapping("/region/save")
	public ResponseEntity<Object> saveRegion(@RequestBody String body) {
		try {
			regionService.saveRegion(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save region ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
			
		}
	}
	
	@GetMapping("/region/list")
	public List<Region> listRegion(){
		return regionService.listRegion();
	}
	
	@ResponseBody
	@PostMapping("/region/delete")
	public ResponseEntity<Object> deleteRegion(@RequestBody String body) {
		try {
			regionService.deleteRegion(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete region ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/region/edit")
	public ResponseEntity<Object> getRegion(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		Region region = regionService.getRegion(body);
		Map<String, Object> data = new HashMap<>();
		data.put("id", region.getId());
		data.put("name", region.getName());
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/region/update")
	public ResponseEntity<Object> updateRegion(@RequestBody String body) {
		try {
			regionService.updateRegion(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update regions ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
			
		}
	}
}
