package com.mitec.web.controller.categories;

import java.util.List;
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

import com.mitec.business.dto.DeviceDto;
import com.mitec.business.model.Error;
import com.mitec.business.service.categories.DeviceService;
import com.mitec.business.service.categories.ErrorService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ErrorController extends BaseController {

	@Autowired
	private ErrorService errorService;
	@Autowired
	private DeviceService deviceService;

	@ResponseBody
	@PostMapping("/commonErrors/save")
	public ResponseEntity<Object> saveError(@RequestBody String body) {
		try {
			errorService.saveError(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save commonErrors ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@GetMapping("/commonErrors")
	public ModelAndView commonErrors(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/common-errors");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		List<DeviceDto> listDevice = deviceService.listDeviceCommonError();
		Page<Error> pages = errorService.listAllCommonError(currentPage - 1, currentSize);
		model.addObject("devices", listDevice);
		model.addObject("pages", pages);
		return model;
	}

	@ResponseBody
	@PostMapping("/commonErrors/delete")
	public ResponseEntity<Object> deleteError(@RequestBody String body) {
		try {
			errorService.deleteError(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete commonErrors ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}

	@ResponseBody
	@PostMapping("/commonErrors/edit")
	public ResponseEntity<Object> getError(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		return new ResponseEntity<>(errorService.getError(body), HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/commonErrors/update")
	public ResponseEntity<Object> updateError(@RequestBody String body) {
		try {
			errorService.updateError(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Chỉnh sửa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update commonErrors ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}	
}
