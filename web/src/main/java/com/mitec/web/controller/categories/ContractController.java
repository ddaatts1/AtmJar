package com.mitec.web.controller.categories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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

import com.mitec.business.dto.ContractDto;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Bank;
import com.mitec.business.model.Contract;
import com.mitec.business.service.categories.ContractService;
import com.mitec.business.utils.ContractTypeEnum;
import com.mitec.web.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ContractController extends BaseController {
	
	@Autowired
	private ContractService contractService;
	private static final String CONTENT_TYPE = "Content-type";
	private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
	
	@GetMapping("/contracts")
	public ModelAndView contracts(@RequestParam(name = "type") Optional<Integer> type,
			@RequestParam(name = "page") Optional<Integer> pageNumber, @RequestParam(name = "size") Optional<Integer> pageSize) {
		log.debug("");
		ModelAndView model = new ModelAndView("categories/contracts");
		
		List<Bank> listBank = contractService.listAll();
		List<ATM> listAtm = contractService.getAtms();
		
		Page<Contract> pages = contractService.getContracts(type.orElse(ContractTypeEnum.HOP_DONG_TAP_TRUNG.getKey()), pageNumber.orElse(appProperties.getDefaultPage()) - 1, pageSize.orElse(appProperties.getDefaultPageSize()));
		
		model.addObject("banks", listBank);
		model.addObject("atms", listAtm);
		model.addObject("pages", pages);
		model.addObject("type", type.orElse(ContractTypeEnum.HOP_DONG_TAP_TRUNG.getKey()));
		return model;
	}
	
	@ResponseBody
	@PostMapping("/contracts/save")
	public ResponseEntity<Object> saveContract(@RequestBody String body) {
		try {
			contractService.saveContract(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Thêm mới thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@ResponseBody
	@PostMapping("/contracts/delete")
	public ResponseEntity<Object> deleteContract(@RequestBody String body) {
		try {
			contractService.deleteContract(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Xóa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			String err = e.getMessage();
			return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping("/contracts/edit")
	public ResponseEntity<Object> editContract(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		ContractDto contract = contractService.getContract(body);
		return new ResponseEntity<>(contract, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/contracts/update")
	public ResponseEntity<Object> updateContract(@RequestBody String body) {
		try {
			contractService.updateContract(body);
			
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Chỉnh sửa thành công\"}", configHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@ResponseBody
	@PostMapping(value = "/contracts/searchExcle")
	public ResponseEntity<Object> statisticalRegionExcle(@RequestBody String body) throws JSONException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, EXCEL_CONTENT_TYPE);
			System.out.println(body);
			return new ResponseEntity<>(contractService.exportContractAtm(body), headers, HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	@ResponseBody
	@PostMapping("/contracts/list-atm")
	public ResponseEntity<Object> listContractATM(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		ContractDto listATM = contractService.listContractATM(body);
		return new ResponseEntity<>(listATM, HttpStatus.OK);
	}






	@ResponseBody
	@PostMapping("/import-atm-contract")
	public ResponseEntity<Object> importAtmContract(@RequestParam(name = "inputFile") MultipartFile excelFile,@RequestParam(name = "id") Long id)  {
	
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, EXCEL_CONTENT_TYPE);
			if (contractService.importAtmContract(excelFile,id).join().equals(true)) {
				return new ResponseEntity<>("Import dữ liệu thành công!", headers, HttpStatus.OK);
			}else {
				return new ResponseEntity<>("Lỗi format excle!", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
	
	@ResponseBody
	@PostMapping("/contracts/list-email")
	public ResponseEntity<Object> listContractEmail(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
		List<Map<String, Object>> list = contractService.listContractEmail(body);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/contracts/delete-email")
	public ResponseEntity<Object> deleteContractEmail(@RequestBody String body) {
		try {
			contractService.deleteContractEmail(body);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
		}
	}
	
	@ResponseBody
	@PostMapping("/contracts/add-email")
	public ResponseEntity<Object> addContractEmail(@RequestBody String body) {
		try {
			contractService.addContractEmail(body);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
		}
	}
	
	@ResponseBody
	@PostMapping("/contractAtms/edit")
	public ResponseEntity<Object> getContractAtm(@RequestBody String body) {
		try {
			return new ResponseEntity<>(contractService.getContractAtm(body), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
		}
	}
	
	@ResponseBody
	@PostMapping("/contractAtms/save")
	public ResponseEntity<Object> saveContractAtm(@RequestBody String body) {
		try {
			log.debug("============>" + body);
			contractService.saveContractAtm(body);
			return new ResponseEntity<>("{\"status\":\"success\",\"messages\":\"Chỉnh sửa thành công\"}", configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
		}
	}
}
