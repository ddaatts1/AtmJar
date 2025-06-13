package com.mitec.web.controller.categories;

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

import com.mitec.business.model.Device;
import com.mitec.business.service.categories.DeviceService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DeviceController extends BaseController {
	
	@Autowired
	private DeviceService deviceService;
	
	@GetMapping("/devices")
	public ModelAndView deviceView(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/devices");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<Device> pages = deviceService.listAllDevice(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		return model;
	}
	
	@ResponseBody
	@PostMapping("/device/save")
	public ResponseEntity<Object> saveDevice(@RequestBody String body) {
		try {
			deviceService.saveDevice(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save device ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}

	@ResponseBody
	@PostMapping("/device/delete")
	public ResponseEntity<Object> deleteDevice(@RequestBody String body) {
		try {
			deviceService.deleteDevice(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete devices ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/device/edit")
	public ResponseEntity<Object> getDevice(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		return new ResponseEntity<>(deviceService.getDevice(body), HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/device/update")
	public ResponseEntity<Object> updateDevice(@RequestBody String body) {
		try {
			deviceService.updateDevice(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update device ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
}
