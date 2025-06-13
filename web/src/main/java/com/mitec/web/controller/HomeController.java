package com.mitec.web.controller;

import java.io.IOException;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
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

import com.mitec.business.dto.HomeDataDto;
import com.mitec.business.service.HomeService;
import com.mitec.business.service.ImportDataService;

@Controller
public class HomeController extends BaseController{
	@Autowired
	private ImportDataService importDataService;
	@Autowired
	private HomeService homeService;

	@GetMapping(value = {"/", "/home"})
	public ModelAndView homeView(@RequestParam(name = "address") Optional<String> address,
			@RequestParam(name = "contract", required = false) Long contractId,
			@RequestParam(name = "no_maintenance_yet", required = false) boolean noMaintenanceYet, 
			@RequestParam(name = "not_closed_yet", required = false) boolean notClosedYet, 
			@RequestParam(name = "repeat_error", required = false) boolean repeatError, 
			@RequestParam(name = "page") Optional<Integer> pageNumber,
			@RequestParam(name = "size") Optional<Integer> pageSize)  {
		
		ModelAndView model = new ModelAndView("home");
		model.addObject("contracts", homeService.getContracts());
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());

		Page<HomeDataDto> pages = homeService.getHomeData(address.orElse(""), contractId, noMaintenanceYet, notClosedYet, repeatError, currentPage-1, currentSize);
		
		model.addObject("address", address.orElse(null));
		model.addObject("contract_id", contractId);
		model.addObject("pages", pages);
		model.addObject("no_maintenance_yet", noMaintenanceYet);
		model.addObject("not_closed_yet", notClosedYet);
		model.addObject("repeat_error", repeatError);
		return model;
	}
	
	@GetMapping(value = {"/system-configuration"})
	public ModelAndView systemConfigurationView() {
		ModelAndView model = new ModelAndView("system-configuration");
		model.addObject("config", homeService.getEmailConfig());
		model.addObject("internalEmail", homeService.getInternalEmail());
		return model;
	}
	
	@ResponseBody
	@PostMapping("/system-configuration/save")
	public ResponseEntity<Object> saveEmailConfig(@RequestBody String body) throws JSONException {
		try {
			homeService.saveEmailConfig(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@ResponseBody
	@PostMapping("/system-configuration/update")
	public ResponseEntity<Object> updateEmailConfig(@RequestBody String body) throws JSONException {
		try {
			homeService.updateEmailConfig(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}

	@ResponseBody
	@PostMapping("/import-data") 
	public ResponseEntity<Object> importData(@RequestBody String body) throws InvalidFormatException, IOException, JSONException {
		importDataService.importData(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping("/fix-clone-statistical") //thêm dữ liệu statiscal ban đầu bị thiếu
	public ResponseEntity<Object> importData() throws InvalidFormatException, IOException, JSONException {
		importDataService.cloneMissedStatistical();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
