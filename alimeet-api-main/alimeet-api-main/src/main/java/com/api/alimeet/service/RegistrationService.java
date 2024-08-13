package com.api.alimeet.service;

import java.util.Map;

import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;

public interface RegistrationService {
	public String getLoginDetail();

	public Map<String, Object> saveUser(UserDto userDto, String action);

	public UserEntity populateUserData(UserDto userDto);
	
	public String verifyUser(String email, String password);
	
	public Map<String, Object> verifyUserAPI(String email, String password);
	
	public String forgetPassword(String email);
	
	public Map<String, Object> forgetPasswordAPI(String email);
	
	public String resetPassword(String email, String password, String oldPassword);
	
	public Map<String, Object> resetPasswordAPI(String email, String password, String oldPassword);
	
}
