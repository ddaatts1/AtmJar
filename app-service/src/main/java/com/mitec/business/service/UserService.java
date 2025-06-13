package com.mitec.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitec.business.dto.ResultApi;
import com.mitec.business.dto.UserDto;
import com.mitec.business.model.Role;
import com.mitec.business.model.User;
import com.mitec.business.repository.RoleRepository;
import com.mitec.business.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;
 	
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public ResultApi changePassword(String input) throws JsonProcessingException {
		log.debug("Processing changePassword()...");
		
		ObjectMapper objectMapper = new ObjectMapper();
		User user = objectMapper.readValue(input, User.class);
		
		ResultApi result = new ResultApi();
		User appUser = userRepository.findByUsername(user.getUsername());
		if(appUser != null) {
//			if(!checkPassword(appUser.getPassword(), user.getPassword())) {
//				result.setMessage("Mật khẩu bạn nhập không đúng!");
//				result.setSuccess(false);
//			}else {
				appUser.setPassword(passwordEncoder.encode(user.getNewPwd()));
				userRepository.save(appUser);
				result.setMessage("Đổi mật khẩu thành công!");
				result.setSuccess(true);
//			}
		}else {
			result.setMessage("Đổi mật khẩu không thành công!");
			result.setSuccess(false);
		}
		
		return result;
	}
	
	public boolean save(User user) {
		List<Role> roles = new ArrayList<>();
		if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
			roles = user.getRoleIds().stream().map(rId -> roleRepository.getById(rId)).collect(Collectors.toList());
			user.setRoles(roles);
		}
		if (user.getId() == null) {
			if (checkUsername(user.getUsername())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}else {
				return false;
			}
		}
		
		if (StringUtils.isBlank(user.getDeviceId())) {
			user.setDeviceId(null);
		}
		userRepository.save(user);
		return true;
	}
	
	private boolean checkUsername(String username) {
		return userRepository.findByUsername(username) == null;
	}
	
	public User getByUsernameAndDeviceId(String username, String deviceId) {
		return userRepository.findByUsernameAndDeviceId(username, deviceId);
	}
	
	public List<User> getAll() {
		return userRepository.findAll(Sort.by("id").descending());
	}
	
	public Optional<User> get(Long id) {
		return userRepository.findById(id);
	}
	
	public Page<UserDto> getAllPage(int pageNumber, int size, String username) {
		List<UserDto> list = userRepository.getPages(username);
		
		int firstIndex = pageNumber*size;
		int lastIndex = (pageNumber + 1)*size >= list.size() ? (list.size()) : (pageNumber + 1)*size;
		List<UserDto> subList = new ArrayList<>();
		if (pageNumber <= (list.size() / size)) {
			subList = list.subList(firstIndex, lastIndex);
		}
		return new PageImpl<>(subList, PageRequest.of(pageNumber, size), list.size());
	}

	public boolean isFirstTimeLogin(String username, String deviceId, String password) {
		User user = userRepository.findByUsername(username);
		if(passwordEncoder.matches(password, user.getPassword()) && StringUtils.isBlank(user.getDeviceId()) && user.isActived()) {
			user.setDeviceId(deviceId);
			userRepository.save(user);
			return true;
		}
		return false;
	}
	
	public void saveDeviceId(String username, String deviceId) {
		User user = userRepository.findByUsername(username);
		user.setDeviceId(deviceId);
		userRepository.save(user);
	}
	
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}
	
	public User getForForm(Long id) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			if (user.getRoles() != null && !user.getRoles().isEmpty()) {
				List<Long> roleIds = user.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList());
				user.setRoleIds(roleIds);
			}
		}
		
		return user;
	}
	
	public void removeDevice(String body) throws JSONException {
		log.debug("Processing removeDevice service()....");
		JSONObject ob = new JSONObject(body);
		
		String username = ob.getString("username");
		
		User user = userRepository.findByUsername(username);
		user.setDeviceId(null);
		
		userRepository.save(user);
	}
	
	public void deleteUser(String body) throws JSONException {
		log.debug("Processing deleteUser service()....");
		JSONObject ob = new JSONObject(body);
		userRepository.deleteByUsername(ob.getString("username"));
	}
	
	public boolean deleteUserByUsername(String username) {
		log.debug("Processing deleteUser service()....");
		try {
			userRepository.deleteByUsername(username);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	public boolean changePassword(User user) {
		User newUser = userRepository.findByUsername(user.getUsername());
		if (passwordEncoder.matches(user.getPassword(), newUser.getPassword())) {
			newUser.setPassword(passwordEncoder.encode(user.getNewPwd()));
			userRepository.save(newUser);
			return true;
		}
		
		return false;
	}

	public User getUserById(Long id){
		return userRepository.findById(id).orElse(null);
	}

	public User getUserByUserName(String username){
		return userRepository.findByUsername(username);
	}
}
