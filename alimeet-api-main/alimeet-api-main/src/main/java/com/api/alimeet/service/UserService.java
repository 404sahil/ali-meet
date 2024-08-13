package com.api.alimeet.service;

import java.util.List;
import java.util.Map;

import com.api.alimeet.model.UserEntity;

public interface UserService {
	
	public String editUserProfile (UserEntity userEntity);
	
	public Map<String, Object> getAllUsers(String role);
	
	public List<UserEntity> getUserDataByEmail(String searchText);
	
	public String deleteUser (Long userId);
	
	public String editUser (Long userId, String userName, String password, String role);
	
	public Map<String, Object> editUserAPI (Long userId, String userName, String password, String role);
	
}
