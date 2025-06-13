package com.mitec.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mitec.business.model.User;
import com.mitec.business.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login")
	public ModelAndView loginView() {
		return new ModelAndView("login");
	}
	
	@GetMapping("/change-pwd")
	public ModelAndView changePwd(@RequestParam(name = "username") String username) {
		ModelAndView model = new ModelAndView("change-pwd");
		
		model.addObject("username", username);
		return model;
	}
	
	@PostMapping("/change-pwd")
	public String savePwdChanged(@ModelAttribute(name = "user") User user, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors().toString());
		}
		try {
			if (userService.changePassword(user)) {
					redirectAttributes.addFlashAttribute("messages", "Đổi mật khẩu thành công!");
			}else {
				redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
				return "redirect:/home";
			}
		}catch (Exception e) {
			redirectAttributes.addFlashAttribute("errors", "Đã có lỗi xảy ra vui lòng thực hiện lại");
			return "redirect:/home";
		}
		
		return "redirect:/home";
	}
}
