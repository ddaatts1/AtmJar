package com.mitec.web.controller.categories;

import java.util.HashMap;
import java.util.Map;
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

import com.mitec.business.model.Series;
import com.mitec.business.service.categories.SeriesService;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class
SeriesController extends BaseController{

	@Autowired
	private SeriesService seriesService;
	
	@GetMapping("/series")
	public ModelAndView series(@RequestParam("page") Optional<Integer> pageNumber,
			@RequestParam("size") Optional<Integer> pageSize) {
		ModelAndView model = new ModelAndView("categories/series");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<Series> pages = seriesService.listAllSeries(currentPage - 1, currentSize);
		model.addObject("pages", pages);
		return model;
	}

	@ResponseBody
	@PostMapping("/series/save")
	public ResponseEntity<Object> saveSeries(@RequestBody String body) {
		try {
			seriesService.saveSeries(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log save series ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/series/delete")
	public ResponseEntity<Object> deleteSeries(@RequestBody String body) {
		try {
			seriesService.deleteSeries(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log delete series ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
	
	@ResponseBody
	@PostMapping("/series/edit")
	public ResponseEntity<Object> getSeries(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		Series series = seriesService.getSeries(body);
		Map<String, Object> data = new HashMap<>();
		data.put("id", series.getId());
		data.put("name", series.getName());
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/series/update")
	public ResponseEntity<Object> updateSeries(@RequestBody String body) {
		try {
			seriesService.updateSeries(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Cập nhật thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Log update series ============> " + e.toString());
			return new ResponseEntity<>("{\"status\":\"error\",\"messages\":\"Đã có lỗi xảy ra vui lòng thực hiện lại\"}", configHeaders(), HttpStatus.OK);
		}
	}
}
