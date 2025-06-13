package com.mitec.web.controller.categories;

import java.util.HashMap;
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

import com.mitec.business.model.Bank;
import com.mitec.business.service.categories.BankService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BankController extends BaseController {
	
	@Autowired
	private BankService bankService;
	
	@GetMapping("/banks")
	public ModelAndView banks(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/banks");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<Bank> pages = bankService.listAllBank(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		return model;
	}
	
	@ResponseBody
	@PostMapping("/bank/save")
	public ResponseEntity<Object> saveBank(@RequestBody String body) throws JSONException {
		try {
			bankService.saveBank(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save bank ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/bank/delete")
	public ResponseEntity<Object> deleteBank(@RequestBody String body) throws JSONException {
		try {
			bankService.deleteBank(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete banks ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/bank/edit")
	public ResponseEntity<Object> getBank(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		Bank bank = bankService.getBank(body);
		Map<String, Object> data = new HashMap<>();
		data.put("id", bank.getId());
		data.put("name", bank.getName());
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/bank/update")
	public ResponseEntity<Object> updateBank(@RequestBody String body) throws JSONException {
		try {
			bankService.updateBank(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update banks ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
			
		}
	}
}
