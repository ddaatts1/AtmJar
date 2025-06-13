package com.mitec.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mitec.business.dto.UserDto;
import com.mitec.business.model.User;
import com.mitec.business.service.UserService;
import com.mitec.business.service.categories.DepartmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@GetMapping(value = "/users")
	public ModelAndView userView(@RequestParam(name = "page") Optional<Integer> pageNumber, @RequestParam(name = "size") Optional<Integer> pageSize,
			@RequestParam(value = "search", required = false) String search) {
		log.debug("Processing userView()....");
		ModelAndView model = new ModelAndView("users");
		int currentPage = pageNumber.orElse(appProperties.getDefaultPage());
		int currentSize = pageSize.orElse(appProperties.getDefaultPageSize());
		Page<UserDto> pages = userService.getAllPage(currentPage - 1, currentSize, search);
		
		log.debug("page content == " + pages.getContent());
		
		model.addObject("pages", pages);
		model.addObject("search", search);

		return model;
	}
	
	@GetMapping(value = {"/user/create", "/user"})
	public ModelAndView userForm(@RequestParam(value = "id", required = false) Long id) {
		log.debug("processing userForm()....");
		ModelAndView model = new ModelAndView("user-form");
		User user = new User();
		if(id != null) {
			user = userService.getForForm(id);
		}
		model.addObject("user", user);
		model.addObject("roles", userService.getAllRoles());
		model.addObject("departments", departmentService.listDepartment());
		return model;
	}
	
	@PostMapping(value = "/user/save")
	public String saveUser(@ModelAttribute(name = "user") User user, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors().toString());
		}
		try {
			if (userService.save(user)) {
				if(user.getId() == null) {
					redirectAttributes.addFlashAttribute("messages", "Tạo mới người dùng thành công!");
				}else {
					redirectAttributes.addFlashAttribute("messages", "Cập nhật thông tin người dùng thành công!");
				}
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
				return "redirect:/users";
			}
		}catch (Exception e) {
			log.debug("errorss ============> " + e.toString());
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			return "redirect:/users";
		}
		
		return "redirect:/users";
	}
	
	@PostMapping(value = "/user/delete")
	public ResponseEntity<String> deleteUser(@RequestBody String body) {
		log.debug("Remove deleteUser by user");
		try {
			userService.deleteUser(body);
			return new ResponseEntity<>("Xóa người dùng thành công!", configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/user/removeDevice")
	public ResponseEntity<String> removeDevice(@RequestBody String body) {
		log.debug("Remove deviceId by user");
		try {
			userService.removeDevice(body);
			return new ResponseEntity<>("Xóa thiết bị thành công!", configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/user/delete")
	public String deleteUserByUsername(@RequestParam(name = "username") String username,
			RedirectAttributes redirectAttributes) {
		log.debug("Remove deleteUser by user");
		try {
			if (userService.deleteUserByUsername(username)) {
				redirectAttributes.addFlashAttribute("messages", "Xóa người dùng thành công!");
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
				return "redirect:/users";
			}
		}catch (Exception e) {
			log.debug("errorss ============> " + e.toString());
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			return "redirect:/users";
		}
		
		return "redirect:/users";
	}
}
