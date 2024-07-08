package com.board;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
	
	//스프링빈 생성
	@Bean //반드시 만들어야 할 부분
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		
		///Lambda Expression(람다 표현식)
		// ** --> Ant 표현식
		return (web)-> web.ignoring().antMatchers("/images/**","/profile/**");
	}
	
	
	//스프링빈 생성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http.authorizeRequests()
			.antMatchers("/**").permitAll();
	
			
		//Spring Security 로그인 창 비활성화
		http.formLogin().disable();

		http.csrf().disable();
		http.cors().disable();
		
		return http.build();
	}
	

}
