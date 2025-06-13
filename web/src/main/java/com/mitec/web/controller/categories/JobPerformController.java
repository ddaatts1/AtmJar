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

import com.mitec.business.dto.JobPerformDto;
import com.mitec.business.model.JobPerform;
import com.mitec.business.service.categories.JobPerformService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class JobPerformController extends BaseController {

	@Autowired
	private JobPerformService jobPerformService;
	
	@GetMapping("/job-perform")
	public ModelAndView jobPerformView(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/job-perform");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<JobPerform> pages = jobPerformService.listAllJobPerform(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		return model;
	}
	
	@GetMapping("/job-perform/list")
	public List<JobPerformDto> getJobperforms() {
		return jobPerformService.getJobPerforms();
	}
	
	@ResponseBody
	@PostMapping("/job-perform/save")
	public ResponseEntity<Object> saveJobPerform(@RequestBody String body) {
		try {
			jobPerformService.saveJobPerform(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save job-perform ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}

	@ResponseBody
	@PostMapping("/job-perform/delete")
	public ResponseEntity<Object> deleteJobPerform(@RequestBody String body) {
		try {
			jobPerformService.deleteJobPerform(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete job-perform ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}

	@ResponseBody
	@PostMapping("/job-perform/edit")
	public ResponseEntity<Object> getJobPerform(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		return new ResponseEntity<>(jobPerformService.getJobPerform(body), HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/job-perform/update")
	public ResponseEntity<Object> updateJobPerform(@RequestBody String body) {
		try {
			jobPerformService.updateJobPerform(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update job-perform ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
}
