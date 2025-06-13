package com.mitec.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.mitec.business.model.Inventory;
import com.mitec.business.model.User;
import com.mitec.business.repository.InventoryRepository;
import com.mitec.business.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
	
	public void initPanel(ModelAndView modelAndView) {
		if(modelAndView != null && SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null){
            try {
            	org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            	User appUser = userRepository.findByUsername(user.getUsername());
            	
            	// Danh sách kho
            	List<Inventory> inventories = new ArrayList<>();
            	if (userRepository.isAdmin(user.getUsername())) {
            		inventories = inventoryRepository.findAll();
            	}else {
            		inventories = inventoryRepository.findByUsername(user.getUsername());
            	}
            	
                modelAndView.addObject("uInfo", appUser);
                modelAndView.addObject("uInventories", inventories);
            }catch (Exception e) {
            	log.debug(e.toString());
            }
        }
	}
}
