package com.mitec.web.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.mitec.web.service.UserDetailsServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
    private DataSource dataSource;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	http.csrf().disable();
    	
    	http.authorizeRequests().antMatchers("/login", "/logout").permitAll();
    	http.authorizeRequests().antMatchers("/change-pwd").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE')");
    	http.authorizeRequests().antMatchers("/import-data", "/fix-clone-statistical").permitAll();	
    	http.authorizeRequests().antMatchers("/", "/home").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE', 'ROLE_MONITOR', 'ROLE_DEPLOYMENT')");
    	http.authorizeRequests().antMatchers("/users", "/users/*").access("hasAnyRole('ROLE_ADMIN')");
    	http.authorizeRequests().antMatchers("/banks", "/regions", "/departments", "/atms", "/series", "/accessories", "/commonErrors").access("hasAnyRole('ROLE_ADMIN')");
    	http.authorizeRequests().antMatchers("/jobs", "/search").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE', 'ROLE_MONITOR')");
    	http.authorizeRequests().antMatchers("/inventories").access("hasAnyRole('ROLE_ADMIN')");
    	http.authorizeRequests().antMatchers("/inventory/**").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE', 'ROLE_MONITOR', 'ROLE_DEPLOYMENT')");
    	http.authorizeRequests().antMatchers("/statistical-amount", "/statistical-amountRegion", "/statistical-amount-per-person", "/statistical", "/statistical-accessories", "/statistical-services").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE', 'ROLE_MONITOR')");
    	http.authorizeRequests().antMatchers("/export/amout-services").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE')");
    	http.authorizeRequests().antMatchers("/system-configuration").access("hasAnyRole('ROLE_ADMIN')");
    	http.authorizeRequests().antMatchers("/contracts").access("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINISTRATIVE', 'ROLE_MONITOR')");
    	http.authorizeRequests().antMatchers("/contracts/delete-email", "/contracts/add-email", "/contractAtms/save", "/contracts/update", "/contracts/delete", "/contracts/save").access("hasAnyRole('ROLE_ADMIN')");
    	http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
    	
    	// fake api 
    	http.authorizeRequests().antMatchers(HttpMethod.POST, "/fake/**").permitAll();
    	
    	http.authorizeRequests().and().formLogin()
	        .loginProcessingUrl("/j_spring_security_check") 
	        .loginPage("/login")
	        .successHandler(customSuccessHandler())
	        .failureHandler(customFailureHandler())
	        .usernameParameter("username")
	        .passwordParameter("password")
	        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
    	
    	//Remember me
    	http.authorizeRequests().and()
	        .rememberMe().tokenRepository(this.persistentTokenRepository())
	        .tokenValiditySeconds(7 * 24 * 60 * 60);
    	
    }
	
	@Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }
	
	@Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
    	return new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				HttpSession session = request.getSession();
				User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				session.setAttribute("username", user.getUsername());
		        session.setAttribute("authorities", user.getAuthorities());
		        
		        response.setStatus(HttpServletResponse.SC_OK);
		        
		        log.debug("Login success!");
	        	response.sendRedirect(request.getContextPath() + "/home");
			}
		};
    }
    
    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
    	return new AuthenticationFailureHandler() {
			
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.sendRedirect("/login?error=true");
			}
		};
    }
}
