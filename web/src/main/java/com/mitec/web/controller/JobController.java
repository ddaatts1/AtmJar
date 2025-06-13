package com.mitec.web.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mitec.business.dto.AccessoryDto;
import com.mitec.business.dto.JobTimeDto;
import com.mitec.business.model.Job;
import com.mitec.business.service.JobService;
import com.mitec.business.service.categories.AccessoryService;
import com.mitec.business.service.categories.JobPerformService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class JobController extends BaseController {
	
	@Autowired
	private JobService jobService;
	@Autowired
	private JobPerformService jobPerformService;
	@Autowired
	private AccessoryService accessoryService;
	
	@GetMapping("/jobs")
	public ModelAndView jobs(@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime, 
			@RequestParam(name = "page") Optional<Integer> pageNumber, @RequestParam(name = "size") Optional<Integer> pageSize,
			@RequestParam(name = "status", required = false) Integer status, @RequestParam(name = "serviceType") Optional<Long> serviceType) throws ParseException {
		log.debug("Processing userView()....");
		ModelAndView model = new ModelAndView("jobs");
		
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		
		Page<Job> pages = jobService.gets(username, startTime, endTime, currentPage-1, currentSize, status, serviceType.orElse(null));
		
		log.debug("page content ==== " + pages.getContent().size());
		
		model.addObject("serviceTypes", jobPerformService.getJobPerforms());
		model.addObject("pages", pages);
		model.addObject("username", username);
		model.addObject("startTime", startTime);
		model.addObject("endTime", endTime);
		model.addObject("status", status);
		model.addObject("serviceType", serviceType.orElse(null));
		return model;
	}
	
	
	@GetMapping(value = {"/search"})
	public ModelAndView lookup(@RequestParam(name = "serial", required = false) String serial,
			@RequestParam(name = "address", required = false) String address, 
			@RequestParam(name = "startTime", required = false) String startTime, 
			@RequestParam(name = "endTime", required = false) String endTime, 
			@RequestParam(name = "page") Optional<Integer> pageNumber,
			@RequestParam(name = "size") Optional<Integer> pageSize,
			@RequestParam(name = "status", required = false) Integer status, 
			@RequestParam(name = "serviceType") Optional<Long> serviceType,
			//Thành thêm accessory
			@RequestParam(name = "accessory", required = false) Integer accessory) throws ParseException {
		log.debug("Processing userView()....");
		ModelAndView model = new ModelAndView("search");
		
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());

		Page<Job> pages = jobService.lookup(serial, address, startTime, endTime, currentPage-1, currentSize, status, accessory, serviceType.orElse(null));
		List<AccessoryDto> listAccessory = accessoryService.listAccessories();

		model.addObject("serviceTypes", jobPerformService.getJobPerforms());
		model.addObject("pages", pages);
		model.addObject("serial", serial);
		model.addObject("address", address);
		model.addObject("startTime", startTime);
		model.addObject("endTime", endTime);
		model.addObject("status", status);
		model.addObject("accessoryValue", accessory);
		model.addObject("accessoryList", listAccessory);
		model.addObject("serviceType", serviceType.orElse(null));
		return model;
	}
	
	@ResponseBody
	@GetMapping(value = "/getJob")
	public ResponseEntity<Object> getJob(@RequestParam(name = "jobId") Long jobId) {

		return new ResponseEntity<>(jobService.jobDetail(jobId), HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "/getAccessories")
	public List<AccessoryDto> getAccessories() {
		return accessoryService.listAccessories();
	}
	
	@GetMapping(value = "/job/delete")
	public String deleteJob(@RequestParam(name = "id") Long id,
			RedirectAttributes redirectAttributes) {
		log.debug("Remove deleteUser by user");
		try {
			if (jobService.deleteJob(id)) {
				redirectAttributes.addFlashAttribute("messages", "Xóa task công việc thành công!");
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			}
		}catch (Exception e) {
			log.debug(	e.toString());
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			return "redirect:/jobs";
		}
		
		return "redirect:/jobs";
	}
	
	@GetMapping(value = "/job/deleteAll")
	public String deleteJob(@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "startTime", required = false) String startTime, 
			@RequestParam(name = "endTime", required = false) String endTime,
			@RequestParam(name = "status", required = false) Integer status,
			@RequestParam(name = "serviceType", required = false) Long serviceType,
			RedirectAttributes redirectAttributes) {
		log.debug("Remove deleteUser by user");
		try {
			if (jobService.deleteAllJob(username, startTime, endTime, status, serviceType)) {
				redirectAttributes.addFlashAttribute("messages", "Xóa task công việc thành công!");
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			}
		}catch (Exception e) {
			log.debug("errorss ============> " + e.toString());
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
		}
		
		return "redirect:/jobs";
	}
	
	@GetMapping(value = "/search/delete")
	public String deleteJobOnSearchView(@RequestParam(name = "id") Long id,
			RedirectAttributes redirectAttributes) {
		log.debug("Remove deleteUser by user");
		try {
			if (jobService.deleteJob(id)) {
				redirectAttributes.addFlashAttribute("messages", "Xóa task công việc thành công!");
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			}
		}catch (Exception e) {
			log.debug("errorss ============> " + e.toString());
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
		}
		
		return "redirect:/search";
	}
	
	@GetMapping(value = "/search/deleteAll")
	public String deleteJobOnSearchView(@RequestParam(name = "serial", required = false) String serial,
			@RequestParam(name = "address", required = false) String address, 
			@RequestParam(name = "startTime", required = false) String startTime, 
			@RequestParam(name = "endTime", required = false) String endTime,
			@RequestParam(name = "status", required = false) Integer status,
			@RequestParam(name = "serviceType", required = false) Long serviceType,
			
			//Thành thêm accessory
			@RequestParam(name = "accessory", required = false) Integer accessory,
			RedirectAttributes redirectAttributes) {
		log.debug("Remove deleteUser by user");
		try {
			if (jobService.deleteAllJob(serial, address, startTime, endTime, status, accessory, serviceType)) {
				redirectAttributes.addFlashAttribute("messages", "Xóa task công việc thành công!");
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			}
		}catch (Exception e) {
			log.debug("errorss ============> " + e.toString());
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
		}
		
		return "redirect:/search";
	}
	
	@ResponseBody
	@PostMapping("/jobs/take-report")
	public ResponseEntity<Object> takeReport(@RequestBody String body) {
		try {
			jobService.takeReport(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Nhận báo cáo thành công!\"}", configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			log.debug(e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Nhận báo cáo thất bại!\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/search/export-excel")
	public ResponseEntity<Object> exportExcel(@RequestBody String body) {
		try {
			return new ResponseEntity<>(jobService.exportExcel(body), excelHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@GetMapping("/jobs/get-time")
	@ResponseStatus(code = HttpStatus.OK)
	public JobTimeDto getJobTime(@RequestParam(name = "jobId") Long jobId) {
		return jobService.getJobTime(jobId);
	}
	
	@ResponseBody
	@PostMapping("/jobs/edit-time")
	@ResponseStatus(code = HttpStatus.OK)
	public String getJobTime(@RequestParam(name = "jobId") Long jobId, @RequestParam(name = "checkIn") String checkInStr, @RequestParam(name = "checkOut") String checkOutStr) {
		return jobService.editJobTime(jobId, checkInStr, checkOutStr);
	}
}
