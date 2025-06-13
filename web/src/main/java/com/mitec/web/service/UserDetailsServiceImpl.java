package com.mitec.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mitec.business.model.User;
import com.mitec.business.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Processing loadUserByUsername()....");
		User appUser = userService.getUserByUsername(username);
		List<String> roleNames = new ArrayList<>();
		if(appUser == null) {
			throw new UsernameNotFoundException("User " + username + "find not found");
		}else {
			if(!appUser.isActived()) {
				throw new UsernameNotFoundException("User " + username + "is not actived");
			}
			else 
				if(appUser.getRoles() != null && !appUser.getRoles().isEmpty()) {
				roleNames = appUser.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
			}
		}
		List<GrantedAuthority> authorities = new ArrayList<>();
		if(!roleNames.isEmpty()) {
			roleNames.stream().forEach(role -> {
				GrantedAuthority authority = new SimpleGrantedAuthority(role);
				authorities.add(authority);
				log.debug("role ==>" + role);
			});
		}
		
		return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), authorities);
	}
}
