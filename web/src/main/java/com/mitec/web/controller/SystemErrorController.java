package com.mitec.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SystemErrorController {
	
	@GetMapping(value = "/403")
    public ModelAndView accessDenied() {
		log.debug("========> accessDenied()...");
		ModelAndView model = new ModelAndView( "commons/error");
		model.addObject("errorCode", "403");
		model.addObject("errorMessage", "Truy cập bị từ chối");
        return model;
    }
}
