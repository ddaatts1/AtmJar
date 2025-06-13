package com.mitec.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class ResourceConfig implements WebMvcConfigurer {
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:/opt/mitec/resources/");
        
        registry.addResourceHandler("swagger-ui.html")
	      .addResourceLocations("classpath:/META-INF/resources/");

	    registry.addResourceHandler("/webjars/**")
	      .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
	
//	@Override
//    public void addCorsMappings(CorsRegistry registry) {
//       registry
//               .addMapping("/**")
//               .allowedMethods("OPTIONS", "GET", "PUT", "POST", "DELETE")
//               .allowedOrigins("http://127.0.0.1:9101/")
//               .allowedHeaders("*");
//    }
}
