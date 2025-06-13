package com.mitec.web.controller.categories;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.mitec.business.model.AddressUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mitec.business.dto.ATMDto;
import com.mitec.business.dto.SeriesDto;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Department;
import com.mitec.business.model.Region;
import com.mitec.business.service.StatisticalService;
import com.mitec.business.service.categories.AtmService;
import com.mitec.business.service.categories.DepartmentService;
import com.mitec.business.service.categories.RegionService;
import com.mitec.business.service.categories.SeriesService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class AtmController extends BaseController {

	@Autowired
	private AtmService atmService;
	@Autowired
	private SeriesService seriesService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private StatisticalService statisticalService;

	@GetMapping("/atms")
	public ModelAndView atms(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize,
			@RequestParam(value = "serial_number", required = false) String serialNumber,
			@RequestParam(value = "address_search", required = false) String addressSearch,
			@RequestParam(value = "series", required = false) Long series,
			@RequestParam(value = "region", required = false) Long region,
			@RequestParam(value = "department", required = false) Long department) {
		ModelAndView model = new ModelAndView("categories/atms");
		List<SeriesDto> listSeries = seriesService.listSeries();
		model.addObject("series", listSeries);
		List<Region> listRegions = regionService.listRegion();
		model.addObject("regions", listRegions);

		List<Department> listDepartments = departmentService.listDepartment();
		model.addObject("departments", listDepartments);
		model.addObject("atmStatus", atmService.getAtmStatusEnum());
		if (serialNumber == null) {
			serialNumber = "";
		}
		if (addressSearch == null) {
			addressSearch = "";
		}
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<ATM> pages = atmService.listAllATM(currentPage - 1, currentSize, serialNumber, addressSearch, series, region, department);
		model.addObject("serialNumberParam", serialNumber);
		model.addObject("addressSearchParam", addressSearch);
		model.addObject("seriesParam", series);
		model.addObject("regionParam", region);
		model.addObject("departmentParam", department);
		model.addObject("pages", pages);
		
		return model;
	}
	
	@ResponseBody
	@PostMapping("/atms/save")
	public ResponseEntity<Object> saveATM(@RequestBody String body) {
		try {
			if (atmService.checkSerialNumberATM(body)) {
				atmService.saveATM(body);
				return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Serial Number đã tồn tại\"}", configHeaders(), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.debug("Log save atms ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/atms/delete")
	public ResponseEntity<Object> deleteATM(@RequestBody String body) {
		try {
			atmService.deleteATM(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			log.debug("Log delete atms ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/atms/edit")
	public ResponseEntity<Object> getATM(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		ATMDto atm = atmService.getATM(body);
		return new ResponseEntity<>(atm, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/atms/update")
	public ResponseEntity<Object> updateATM(@RequestBody String body) {
		try {
			atmService.updateATM(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Chỉnh sửa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			log.debug("Log update atms ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
			
		}
	}
	
	@ResponseBody
	@PostMapping("/import-atm-data")
	public ResponseEntity<Object> importAtmData(@RequestParam(name = "inputFile") MultipartFile excelFile) {
		Boolean check = atmService.importAtmData(excelFile);
		if (check.equals(true)) {
			return new ResponseEntity<>("Import dữ liệu thành công!", configHeaders(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Import dữ liệu không thành công!", configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping(value = "/atm-region/search")
	public ResponseEntity<Object> statisticalRegion(@RequestBody String body) throws JSONException, InterruptedException, ExecutionException {
		CompletableFuture<List<Department>> list = statisticalService.getRegion(body);
		CompletableFuture.allOf(list).join();
		
		return new ResponseEntity<>(list.get(), HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping(value = "/atm-region/searchExcle")
	public ResponseEntity<Object> statisticalRegionExcle(@RequestBody String body) throws JSONException {
		try {
			return new ResponseEntity<>(atmService.exportTemplateAtm(body), configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	
	@ResponseBody
	@GetMapping("/atms/change-status")
	public ResponseEntity<Object> changeStatus(@RequestParam("serialNumber") String serial) {
		try {
			atmService.changeStatus(serial);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thay đổi trạng thái thành công!\"}", configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			log.debug(e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Thay đổi trạng thái thất bại!\"}", configHeaders(), HttpStatus.OK);
		}
	}

	@PutMapping("/update-address")
	public ResponseEntity<ATM> updateAtmAddress(@RequestBody AddressUpdateRequest request
	) {
		try {
			ATM updatedAtm = atmService.updateAddress(request,"hoandg");
			return new ResponseEntity<>( HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}


}
