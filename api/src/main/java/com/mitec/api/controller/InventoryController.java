package com.mitec.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitec.api.service.AuthorizationService;
import com.mitec.business.service.api.InventoryApiService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class InventoryController {
	
	private HttpHeaders configHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-type", "application/json; charset=UTF-8");
		
		return headers;
	}
	
	@Autowired
	private InventoryApiService inventoryService;

	@GetMapping("/inventories")
	public ResponseEntity<Object> getInventories(HttpServletRequest req) {
		log.debug("token ====> " + req.getHeader("Authorization"));
		log.debug(AuthorizationService.getUser(req.getHeader("Authorization")));

		return new ResponseEntity<>(inventoryService.getInventoriesByUser(AuthorizationService.getUser(req.getHeader("Authorization"))), configHeaders(), HttpStatus.OK);
	}
}
