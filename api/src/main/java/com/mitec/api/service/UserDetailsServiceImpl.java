package com.mitec.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mitec.business.model.User;
import com.mitec.business.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
		log.debug("username input: " + input);
		String[] split = input.split(":");
		String username = split[0];
	    String deviceId = split[1];
	    
		User appUser = userRepository.findByUsername(username);
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		List<String> roleNames = new ArrayList<>();
		
		if(appUser == null) {
			throw new UsernameNotFoundException("User " + username + "was not found");
		}else {
			if(appUser.getRoles() != null && !appUser.getRoles().isEmpty()) {
				roleNames = appUser.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
			}
		}
		
		if(!roleNames.isEmpty()) {
			if (roleNames.contains("ROLE_DEPLOYMENT")) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_DEPLOYMENT"));
				if(StringUtils.isBlank(appUser.getDeviceId())) {
					appUser.setDeviceId(deviceId);
					appUser = userRepository.save(appUser);
//				}else 
//					if(!appUser.getDeviceId().equals(deviceId)){
//					throw new UsernameNotFoundException("Thiết bị này chưa được đăng ký");
				}
			}else 
				throw new UsernameNotFoundException("User " + username + "does not have permission");
		}else {
			throw new UsernameNotFoundException("User " + username + "does not have permission");
		}
		return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), appUser.isActived(), true, true, true, grantedAuthorities);
	}

}
