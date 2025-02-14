package com.board;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	
		registry.addResourceHandler("/profile/**") //웹상의 URL로 표현되는 상대 경로
				.addResourceLocations("file:///c:/Repository/profile");
		
		registry.addResourceHandler("/**") 
				.addResourceLocations("classpath:/static/");
		
}

}