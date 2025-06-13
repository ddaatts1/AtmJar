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

import com.mitec.business.dto.ErrorDeviceDto;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.service.categories.ErrorDeviceService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ErrorDeviceController extends BaseController {
	
	@Autowired
	private ErrorDeviceService errorDeviceService;
	
	@GetMapping("/error-devices")
	public ModelAndView devices(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/error-devices");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<ErrorDevice> pages = errorDeviceService.listAllErrorDevice(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		return model;
	}
	
	@ResponseBody
	@PostMapping("/error-device/save")
	public ResponseEntity<Object> saveErrorDevice(@RequestBody String body) {
		try {
			errorDeviceService.saveErrorDevice(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save error-device ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/error-device/delete")
	public ResponseEntity<Object> deleteErrorDevice(@RequestBody String body) {
		try {
			errorDeviceService.deleteErrorDevice(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete error-device ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/error-device/edit")
	public ResponseEntity<Object> getErrorDevice(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		ErrorDeviceDto device = errorDeviceService.getErrorDevice(body);
		return new ResponseEntity<>(device, HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/error-device/update")
	public ResponseEntity<Object> updateErrorDevice(@RequestBody String body) {
		try {
			errorDeviceService.updateErrorDevice(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update error-device ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
}
