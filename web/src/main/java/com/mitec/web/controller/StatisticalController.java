package com.mitec.web.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.mitec.business.dto.ATMForStatistical;
import com.mitec.business.dto.StatisticalAmountDto;
import com.mitec.business.dto.StatisticalRegion;
import com.mitec.business.model.Department;
import com.mitec.business.model.Region;
import com.mitec.business.model.Statistical;
import com.mitec.business.service.FileService;
import com.mitec.business.service.StatisticalService;
import com.mitec.business.service.categories.ContractService;
import com.mitec.business.utils.CustomFormatDate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class StatisticalController extends BaseController{
	
	@Autowired
	private StatisticalService statisticalService;
	@Autowired
	private FileService fileService;
	@Autowired
	private ContractService contractService;
	
	@GetMapping(value = {"/statistical"})
	public ModelAndView statisticalView() {
		ModelAndView model = new ModelAndView("statistical/statistical");
		
		List<ATMForStatistical> list = new ArrayList<>();
		
		model.addObject("contracts", statisticalService.getContractByType());
		model.addObject("list", list);
		if (!list.isEmpty()) {
			model.addObject("periods", list.get(0).getPeriods());
		}
		return model;
	}
	
	@ResponseBody
	@PostMapping("/statistical/getContract")
	public ResponseEntity<Object> getContract(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		Map<String, String> contract = contractService.getContractTime(body);
		return new ResponseEntity<>(contract, HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping(value = "/statistical")
	public ResponseEntity<Object> statisticalSearch(@RequestBody String body) {
		try {
			CompletableFuture<List<ATMForStatistical>> list = statisticalService.cyclicalStatistics(body);
			CompletableFuture.allOf(list).join();
			return new ResponseEntity<>(list.get(), HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.OK); 
		}
	}
	
	
	@GetMapping(value = {"/statistical-amount"})
	public ModelAndView statisticalAmountView() {
		
		ModelAndView model = new ModelAndView("statistical/statistical-amount");
		
		model.addObject("contracts", statisticalService.getContracts());
		model.addObject("regions", statisticalService.getRegions());
		model.addObject("departments", statisticalService.getDepartments());
		return model;
	}
	
	@GetMapping("/departments/list")
	public List<Department> listDepartment(){
		return statisticalService.getDepartments();
	}
	
	@GetMapping(value = {"/statistical-amountRegion"})
	public ModelAndView statisticalAmountViewRegion() {
		
		ModelAndView model = new ModelAndView("statistical/statistical-amountRegion");
		
		model.addObject("contracts", statisticalService.getCurrenContracts());
		model.addObject("regions", statisticalService.getRegions());
		model.addObject("departments", statisticalService.getDepartments());
		return model;
	}

	@ResponseBody
	@PostMapping(value = "/statistical-amountRegion/search")
	public ResponseEntity<Object> searchStatisticalRegion(@RequestBody String body) throws JSONException, InterruptedException, ExecutionException {
		JSONObject ob = new JSONObject(body);
		Set<StatisticalRegion> list;
		if(!ob.isNull("status")) {
			list = statisticalService.totalAmountRegionExcel(body);
		}else{
			// dữ liệu ban đầu
			list = statisticalService.totalAmountRegion();
		}
//		CompletableFuture.allOf(list).join();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping(value = "/statistical-region/search")
	public ResponseEntity<Object> statisticalRegion(@RequestBody String body) throws JSONException, InterruptedException, ExecutionException {
		CompletableFuture<List<Department>> list = statisticalService.getRegion(body);
		CompletableFuture.allOf(list).join();
		
		return new ResponseEntity<>(list.get(), HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping(value = "/statistical-departmen/search")
	public ResponseEntity<Object> statisticalDepartmen(@RequestBody String body) throws JSONException, InterruptedException, ExecutionException {
		CompletableFuture<List<Region>> list = statisticalService.getDepartmen(body);
		CompletableFuture.allOf(list).join();
		
		return new ResponseEntity<>(list.get(), HttpStatus.OK);
	}   
	
	
	@GetMapping(value = "/statistical-amount-per-person")
	public ModelAndView statisticalAmountPerPerson() {
		return new ModelAndView("statistical/person-statistical-amount"); 
	}

	@ResponseBody
	@PostMapping(value = "/statistical-amount-per-person")
	public ResponseEntity<Object> statisticalAmountPerPerson(@RequestBody String body) throws JSONException, InterruptedException, ExecutionException {
		CompletableFuture<List<StatisticalAmountDto>> list = statisticalService.personStatistical(body);
		CompletableFuture.allOf(list).join();
		return new ResponseEntity<>(list.get(), HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping(value = "/export/amout-services")
	public ResponseEntity<Object> exportStatisticalAmout(@RequestBody String body) {
		try {
			return new ResponseEntity<>(fileService.statisticalAmountRegion(body), excelHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping(value = "/export/person-amout-services")
	public ResponseEntity<Object> exportPersonStatisticalAmout(@RequestBody String body) {
		try {
			return new ResponseEntity<>(fileService.personStatisticalAmount(body), excelHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping(value = "/export/cyclicalStatistics")
	public ResponseEntity<Object> exportCyclicalStatistics(@RequestBody String body) {
		try {
			byte[] result = fileService.cyclicalStatistics(body);
			if (result != null) {
				return new ResponseEntity<>(result, excelHeaders(), HttpStatus.OK);
			}else {
				return new ResponseEntity<>("Có lỗi xảy ra khi xuất báo cáo!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping(value = "/export/statisticalByMonth")
	public ResponseEntity<Object> exportStatisticalByMonth(@RequestBody String body){
		try {
			byte[] result = fileService.statisticalByMonth(body);
			if (result != null) {
				return new ResponseEntity<>(result, excelHeaders(), HttpStatus.OK);
			}else {
				return new ResponseEntity<>("Có lỗi xảy ra khi xuất báo cáo!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@GetMapping(value = "/statistical/services")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Map<String, Object>> getServiceDetailByUser(@RequestParam(name = "username") String username, 
			@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {

		if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime))
			return statisticalService.getServiceByUser(username);
		
		return statisticalService.getServiceByUser(username, startTime, endTime);
	}
	
	@GetMapping(value = {"/statistical-accessories"})
	public ModelAndView statisticalAccessory(@RequestParam(value = "contractId", required = false) Optional<Long> contractId,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) throws ParseException {
		ModelAndView model = new ModelAndView("statistical/statistical-accessory");
		
		model.addObject("contracts", statisticalService.getContracts());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		long monthsBetween;
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			//format string dd/mm/yyyy về yyyy-mm-dd
			startTime = CustomFormatDate.formatDate(startTime, "dd/MM/yyyy", "yyyy-MM-dd");
			endTime = CustomFormatDate.formatDate(endTime, "dd/MM/yyyy", "yyyy-MM-dd");
			
			monthsBetween = ChronoUnit.MONTHS.between(
				        LocalDate.parse(startTime).withDayOfMonth(1),
				        LocalDate.parse(endTime).withDayOfMonth(1));	
		} else {
			monthsBetween = 5;
		}
		
		List<Long> listMonth = new ArrayList<>();
		for (long i = monthsBetween; i >= 0; i--) {
			LocalDateTime now;
			if (StringUtils.isNotBlank(endTime) && endTime.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
					now = LocalDateTime.parse(endTime + " 00:00:00", formatter);
			} else {
				now = LocalDateTime.now();
			}
			now = now.minusMonths(i);
			int month = now.getMonthValue();
			listMonth.add(Long.parseLong(String.valueOf(month)));
		}
		model.addObject("list_month", listMonth);
		List<Map<String, Object>> list = statisticalService.getListContractATM(contractId.orElse(null), monthsBetween, endTime);
		model.addObject("list", list);
		
		int months = 0;
		if (!list.isEmpty()) {
			@SuppressWarnings("unchecked")
			List<Long> listCount = (List<Long>) list.get(0).get("count");
			months = listCount.size();
		}
		
		List<Long> sumQuantity = new ArrayList<>();
		for (int i = 0; i < months; i++) {
			Long quantityItem = 0L;
			for (Map<String, Object> item : list) {
				@SuppressWarnings("unchecked")
				List<Long> counts = (List<Long>) item.get("count");
				quantityItem += counts.get(i);
			}
			sumQuantity.add(quantityItem);
		}
		model.addObject("sumQuantity", sumQuantity);
		
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			startTime = CustomFormatDate.formatDate(startTime, "yyyy-MM-dd", "dd/MM/yyyy");
			endTime = CustomFormatDate.formatDate(endTime, "yyyy-MM-dd", "dd/MM/yyyy");
		}
		
		model.addObject("startTime", StringUtils.isNotBlank(startTime) ? startTime : CustomFormatDate.todayMinusMonths(5));
		model.addObject("endTime", StringUtils.isNotBlank(endTime) ? endTime : CustomFormatDate.todayString());
		model.addObject("monthsBetween", monthsBetween);
		return model;
	}
	
	@ResponseBody
	@PostMapping(value = "/export/statistical-accessory-excel")
	public ResponseEntity<Object> exportStatisticalAccessory(@RequestBody String body) {
		try {
			return new ResponseEntity<>(fileService.statisticalAccessory(body), excelHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@GetMapping("/statistical-by-month") 
	public ResponseEntity<Object> statisticalByMonth(@RequestParam(name = "contractId", required = false) Long contractId, @RequestParam(name = "regionId", required = false) String regionId,
			@RequestParam(name = "departmentId", required = false) Long departmentId) throws ParseException {
		return new ResponseEntity<>(statisticalService.statiscalAmountServices(contractId, regionId, departmentId, null, null), HttpStatus.OK);
	}
	
	@GetMapping("/statistical-services")
	public ModelAndView statisticalAccessory(@RequestParam(name = "contractId", required = false) Long contractId, 
			@RequestParam(name = "regionId", required = false) String regionId, 
			@RequestParam(name = "departmentId", required = false) Long departmentId,
			@RequestParam(name = "startTime", required = false) String startTime,
			@RequestParam(name = "endTime", required = false) String endTime) throws ParseException {
		
		ModelAndView model = new ModelAndView("statistical/statistical-services");

		Map<Object, Object> result = statisticalService.statiscalAmountServices(contractId, regionId, departmentId, startTime, endTime);
		
		model.addObject("regions", statisticalService.getRegions());
		model.addObject("departments", statisticalService.getDepartments());
		model.addObject("contracts", statisticalService.getContracts());

		model.addObject("data", result.get("data"));
		model.addObject("months", result.get("months"));
		
		model.addObject("contractId", contractId);
		model.addObject("regionId", regionId);
		model.addObject("departmentId", departmentId);
		
		model.addObject("startTime", StringUtils.isNotBlank(startTime) ? startTime : CustomFormatDate.todayMinusMonths(4));
		model.addObject("endTime", StringUtils.isNotBlank(endTime) ? endTime : CustomFormatDate.todayString());
		return model;
	}
	
	@ResponseBody
	@PostMapping("/statistical-services/get-data-chart")
	public ResponseEntity<Object> getDataStatisticalServices(@RequestBody String body) {
		try {
			JSONObject ob = new JSONObject(body);
			
			Long contractId = StringUtils.isBlank(ob.getString("contractId")) ? null : ob.getLong("contractId");
			String regionId = ob.isNull("regionId") ? null : ob.getString("regionId");
			Long departmentId = StringUtils.isBlank(ob.getString("departmentId")) ? null : ob.getLong("departmentId");
			String startTime = StringUtils.isBlank(ob.getString("startTime")) ? null : ob.getString("startTime");
			String endTime = StringUtils.isBlank(ob.getString("endTime")) ? null : ob.getString("endTime");
			
			Map<Object, Object> data = statisticalService.getDataChart(contractId, regionId, departmentId, startTime, endTime);
			
			return new ResponseEntity<>(data, HttpStatus.OK);
		} catch (Exception e) {
			log.debug("Log get data statistical-services ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
}
