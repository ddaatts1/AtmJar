package com.mitec.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mitec.business.dto.PartInventoryDto;
import com.mitec.business.dto.UserDto;
import com.mitec.business.model.Inventory;
import com.mitec.business.model.PartInventory;
import com.mitec.business.model.Tracking;
import com.mitec.business.service.InventoryService;
import com.mitec.business.service.categories.RegionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class InventoryController extends BaseController {
	
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private RegionService regionService;
	
	private static final String CONTENT_TYPE = "Content-type";
	private static final String PDF_CONTENT_TYPE = "application/pdf;charset=utf-8";
	private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

	// Kiểm tra quyền vào kho
	private boolean checkInventoryPermission(Long id) {
		String username = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			username = ((UserDetails) auth.getPrincipal()).getUsername();
		}
		
		return inventoryService.checkInventory(username, id);
	}
	
	private String getUsername() {
		String username = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			username = ((UserDetails) auth.getPrincipal()).getUsername();
		}
		return username;
	}
	
	@GetMapping(value = "/inventories")
	public ModelAndView getViewInventories(@RequestParam(name = "name", required = false) String inventoryName,
			@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size) {
		ModelAndView model = new ModelAndView("inventory/inventories-admin");
		
		Page<Inventory> pages = inventoryService.getInventories(inventoryName, 
				page.orElse(appProperties.getDefaultPage() - 1), size.orElse(appProperties.getDefaultPageSize()));
		
		model.addObject("pages", pages);
		model.addObject("regions", regionService.getRegions());
		model.addObject("users", inventoryService.getUsers());
		return model;
	}
	
	@GetMapping("/inventory")
	public String getInventoryView(Model model, @RequestParam(name = "id") Long id,
			@RequestParam(name = "type") Optional<String> type, @RequestParam(name = "name") Optional<String> name,
			@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size) {
		if (checkInventoryPermission(id)) {
			int pageNumber = page.orElse(appProperties.getDefaultPage());
			int pageSize = size.orElse(appProperties.getDefaultPageSize());
			Page<PartInventory> pages = inventoryService.getPartInventory(id, type.orElse(null), name.orElse(null), pageNumber - 1, pageSize);
			model.addAttribute("pages", pages);
			model.addAttribute("id", id);
			model.addAttribute("inventoryName", inventoryService.getName(id));
			model.addAttribute("type", type.orElse(""));
			model.addAttribute("name", name.orElse(null));
			return "inventory/inventory";
		}
		return "redirect:/403";
	}
	
	@GetMapping("/inventory/goods-issue")
	public ModelAndView goodsIssueView(@RequestParam(name = "inventoryId") Long inventoryId) {
		ModelAndView model = new ModelAndView("inventory/goods-issue");
		model.addObject("inventoryId", inventoryId);
		model.addObject("inventories", inventoryService.getAllButNotId(inventoryId));
		return model;
	}

	@ResponseBody
	@GetMapping("/user-suggestion")
	public ResponseEntity<Object> getUserSuggestion() {
		return new ResponseEntity<>(inventoryService.getUserSuggestions(getUsername()), HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping("/selected-suggestion")
	public ResponseEntity<Object> getSelectedSuggestion(@RequestParam(name = "key") String key) {
		return new ResponseEntity<>(inventoryService.getSelectedSuggestion(key), HttpStatus.OK);
	}
	
	@GetMapping("/inventory/goods-receipt")
	public ModelAndView goodsReceiptView(@RequestParam(name = "inventoryId") Long inventoryId) {
		ModelAndView model = new ModelAndView("inventory/goods-receipt");
		model.addObject("inventoryId", inventoryId);
		return model;
	}
	
	@ResponseBody
	@GetMapping("/inventory/export-template-goods-receipt")
	public ResponseEntity<Object> getTemplateGoodsReceipt() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, EXCEL_CONTENT_TYPE);
			return new ResponseEntity<>(inventoryService.getTemplateGoodsReceipt(), headers, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/inventory/repair-part")
	public String repairPartView(Model model, @RequestParam(name = "inventoryId") Long inventoryId) {
		model.addAttribute("inventoryId", inventoryId);
		model.addAttribute("users", inventoryService.getUserByInventory(inventoryId));
		return "inventory/repair-part";
	}
	
	@ResponseBody
	@GetMapping("/inventory/get-user-data")
	public ResponseEntity<Object> getUserData(@RequestParam(name = "username") String username) throws IllegalAccessException, InvocationTargetException {
		return new ResponseEntity<>(inventoryService.getUserData(username), HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping(value = "/inventories/save")
	public ResponseEntity<Object> createInventories(@RequestBody String body) {
		Map<String, String> result = new HashMap<>();
		try {
			inventoryService.createInventory(body);
			result.put("status", "success");
			result.put("messages", "Tạo mới kho thành công!");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("messages", "Tạo mới kho thất bại!");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping("/inventories/delete")
	public ResponseEntity<Object> deleteInventory(@RequestBody String body) {
		try {
			inventoryService.deleteInventory(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/inventories/getOne")
	public ResponseEntity<Object> getInventory(@RequestParam(name = "id") Long id) {
		try {
			return new ResponseEntity<>(inventoryService.get(id), HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping(value = "/inventories/update")
	public ResponseEntity<Object> updateInventories(@RequestBody String body) {
		Map<String, String> result = new HashMap<>();
		try {
			inventoryService.updateInventory(body);
			result.put("status", "success");
			result.put("messages", "Cập nhật thông tin kho thành công!");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("messages", "Cập nhật thông tin kho thất bại!");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@GetMapping("/inventories/getByType")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PartInventoryDto> getPartByType(@RequestParam(name = "inventoryId") Long inventoryId, @RequestParam(name = "partType") Integer partType) {
		return inventoryService.getPartByType(partType, inventoryId);
	}
	
	@ResponseBody
	@GetMapping("/inventories/get-by-type-and-quantity-gt-zero")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PartInventoryDto> getByTypeAndQuantityGtZero(@RequestParam(name = "inventoryId") Long inventoryId, @RequestParam(name = "partType") Integer partType) {
		return inventoryService.findByTypeAndQuantityGtZero(partType, inventoryId);
	}
	
	
	@ResponseBody
	@GetMapping("/inventories/getQuantity")
	@ResponseStatus(code = HttpStatus.OK)
	public Long getQuantity(@RequestParam(name = "partId") Long partId) {
		return inventoryService.getQuantity(partId);
	}
	
	@ResponseBody
	@GetMapping("/inventories/getUserByInventory")
	@ResponseStatus(code = HttpStatus.OK)
	public List<UserDto> getPartByType(@RequestParam(name = "inventoryId") Long inventoryId) {
		return inventoryService.getUserByInventory(inventoryId);
	}
	
	@ResponseBody
	@GetMapping(value = "/inventories/thong-tin-nguoi-nhan")
	public ResponseEntity<Object> genGoodsIssue(@RequestParam(name = "id") String id) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, PDF_CONTENT_TYPE);
			return new ResponseEntity<>(inventoryService.genGoodsIssue(Long.valueOf(id)), headers, HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//genGoodsIssueDetail
	@ResponseBody
	@GetMapping(value = "/inventories/danh-sach-part-gui-di")
	public ResponseEntity<Object> genGoodsIssueDetail(@RequestParam(name = "id") String id) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, PDF_CONTENT_TYPE);
			return new ResponseEntity<>(inventoryService.genGoodsIssueDetail(Long.valueOf(id)), headers, HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping("/inventory/import-goods-receipt")
	public ResponseEntity<Object> importDataGoodsReceipt(@RequestParam(name = "inputFile") MultipartFile excelFile,
			@RequestParam(name = "parts") String parts, @RequestParam(name = "inventoryId") Long inventoryId) {
		try {
			return new ResponseEntity<>(inventoryService.importReceiptData(excelFile, parts, inventoryId), HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Import dữ liệu không thành công!", configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping("/inventory/goods-receipt/save")
	public ResponseEntity<Object> saveGoodsReceipt(@RequestBody String body) {
		String username = getUsername();
		Map<String, Object> result = new HashMap<>();
		try {
			Tracking tracking = inventoryService.saveGoodsReceipt(body, username);
			result.put("trackingId", tracking.getId());
			result.put("isOpenNewTab", false);
			result.put("status", "success");
			result.put("messages", "Tạo phiếu thành công!");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("messages", "Tạo phiếu thất bại!");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping("/inventory/goods-issue/save")
	public ResponseEntity<Object> saveGoodsIssue(@RequestBody String body) {
		String username = getUsername();
		Map<String, Object> result = new HashMap<>();
		try {
			Tracking tracking = inventoryService.saveGoodsIssue(body, username);
			result.put("trackingId", tracking.getId());
			result.put("isOpenNewTab", true);
			result.put("status", "success");
			result.put("messages", "Tạo phiếu thành công!");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("messages", "Tạo phiếu thất bại!");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@ResponseBody
	@PostMapping("/inventory/repair-part/save")
	public ResponseEntity<Object> saveRepairPart(@RequestBody String body) {
		String username = getUsername();
		Map<String, Object> result = new HashMap<>();
		try {
			Tracking tracking = inventoryService.saveRepairPart(body, username);
			result.put("trackingId", tracking.getId());
			result.put("isOpenNewTab", true);
			result.put("status", "success");
			result.put("messages", "Tạo phiếu thành công!");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("messages", "Tạo phiếu thất bại!");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/inventory/history")
	public String getHistoryView(Model model, @RequestParam(name = "inventoryId") Long inventoryId, @RequestParam(name = "name") Optional<String> name, 
			@RequestParam(name = "status") Optional<String> status, @RequestParam(name = "type") Optional<String> type,
			@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size) {
		if (checkInventoryPermission(inventoryId)) {
			model.addAttribute("pages", inventoryService.getHistory(inventoryId, name.orElse(null), status.orElse(null), type.orElse(null),
					page.orElse(appProperties.getDefaultPage() - 1), size.orElse(appProperties.getDefaultPageSize())));
			
			model.addAttribute("inventoryId", inventoryId);
			model.addAttribute("name", name.orElse(null));
			model.addAttribute("status", status.orElse(null));
			model.addAttribute("type", type.orElse(null));
			return "inventory/history";
		}
		return "redirect:/403";
	}
	
	@ResponseBody
	@GetMapping("/inventory/history/view")
	@ResponseStatus(code = HttpStatus.OK)
	public Tracking getHistoryDetail(@RequestParam(name = "id") Long id) {
		return inventoryService.getTracking(id);
	}
	
	@ResponseBody
	@PostMapping("/inventory/goods-issue/update")
	public ResponseEntity<Object> updateTracking(@RequestBody String body) {
		Map<String, Object> result = new HashMap<>();
		try {
			inventoryService.updateTracking(body);
			result.put("status", "success");
			result.put("messages", "Tạo phiếu thành công!");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("messages", "Tạo phiếu thất bại!");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
