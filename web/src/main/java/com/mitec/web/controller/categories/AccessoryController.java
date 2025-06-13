package com.mitec.web.controller.categories;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mitec.business.dto.AccessoryDto;
import com.mitec.business.dto.SeriesDto;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.service.categories.AccessoryService;
import com.mitec.business.service.categories.ErrorDeviceService;
import com.mitec.business.service.categories.SeriesService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AccessoryController extends BaseController {
	
	@Autowired
	private AccessoryService accessoryService;
	@Autowired
	private SeriesService seriesService;
	@Autowired
	private ErrorDeviceService errorDeviceService;
	
	@ResponseBody
	@PostMapping("/accessories/save")
	public ResponseEntity<Object> saveAccessories(@RequestBody String body) throws JSONException {
		try {
			accessoryService.saveAccessories(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save accessories ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@GetMapping("/accessories")
	public ModelAndView accessories(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/accessories");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		List<ErrorDevice> listDevice = errorDeviceService.listErrorDevice();
		model.addObject("devices", listDevice);
		
		Page<Accessory> pages = accessoryService.listAllAccessories(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		
		List<SeriesDto> listSeries = seriesService.listSeries();
		model.addObject("series", listSeries);
		
		return model;
	}
	
	@ResponseBody
	@PostMapping("/accessories/delete")
	public ResponseEntity<Object> deleteAccessories(@RequestBody String body) {
		try {
			accessoryService.deleteAccessories(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete accessories ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/accesories/edit")
	public ResponseEntity<Object> getAccessories(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		AccessoryDto accessory = accessoryService.getAccessories(body);
		return new ResponseEntity<>(accessory, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/accessories/update")
	public ResponseEntity<Object> updateAccessories(@RequestBody String body) {
		try {
			accessoryService.updateAccessories(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Chỉnh sửa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update accessories ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
			
		}
	}
	
	@ResponseBody
	@PostMapping("/accessories/export")
	public ResponseEntity<Object> exportData() {
		try {
			byte[] result = accessoryService.exportData();
			if (result.length > 0) {
				return new ResponseEntity<>(result, excelHeaders(), HttpStatus.OK);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(excelHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(excelHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ResponseBody
	@PostMapping("/accessories/import")
	public ResponseEntity<Object> importData(@RequestParam(name = "file") MultipartFile file) {
		Map<String, Object> result = accessoryService.importData(file);
		boolean isSuccess = (boolean) result.get("isSuccess");
		if (isSuccess) {
			return new ResponseEntity<>(String.valueOf(result.get("messages")), configHeaders(), HttpStatus.OK);
		}
		return new ResponseEntity<>(String.valueOf(result.get("messages")), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}