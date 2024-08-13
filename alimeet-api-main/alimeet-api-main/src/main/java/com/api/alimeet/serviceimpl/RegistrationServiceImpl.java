package com.api.alimeet.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.alimeet.constant.ApplicationConstant;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.UserRepository;
import com.api.alimeet.service.EmailService;
import com.api.alimeet.service.RegistrationService;

@Service("customerService")
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	EmailService emailService;

	@Override
	public String getLoginDetail() {
		// TODO Auto-generated method stub
		return "getLoginDetail Called";
	}

	public Map<String, Object> saveUser(final UserDto userData, String action) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(action.equalsIgnoreCase("add")) {
			Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(userData.getEmail());
			
			if(!userEntity.isPresent()) {
				UserEntity userModel = populateUserData(userData);
				UserEntity entity = userRepository.save(userModel);
				
				if(entity != null) {
					userData.setPassword(entity.getPassword());
					emailService.sendWelcomeMailToUser(userData);
				}
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.REGISTRATION_SUCCESS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			} else {
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.REGISTRATION_EMAIL_EXISTS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			}
		} else if(action.equalsIgnoreCase("addByAdmin")) {
			
			Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(userData.getEmail());
			
			if(!userEntity.isPresent()) {
				UserEntity userModel = populateUserData(userData);
				userModel.setIsVerified(true);
				UserEntity entity = userRepository.save(userModel);
				
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.REGISTRATION_SUCCESS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			} else {
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.REGISTRATION_EMAIL_EXISTS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			}
			
		} else {
			return map;
		}
	}

	public UserEntity populateUserData(final UserDto userData) {
		UserEntity user = new UserEntity();
		user.setUserName(userData.getUserName());
		user.setEmail(userData.getEmail());
		user.setPassword(passwordEncoder.encode(userData.getPassword()));
		user.setRole(userData.getRole());
		user.setIsVerified(false);
		return user;
	}

	@Override
	public String verifyUser(String email, String password) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(email);
		
		if(userEntity.isPresent()) {
			UserEntity userData = userEntity.get();
			
			if(userData.getPassword().equalsIgnoreCase(password)) {
				userData.setIsVerified(true);
				userRepository.save(userData);
				return "true";
			}
		}
		
		return "false";
		
	}

	@Override
	public String forgetPassword(String email) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(email);
		
		if(userEntity.isPresent()) {
			UserEntity userData = userEntity.get();
			emailService.sendPasswordResetMailToUser(userData);
			return "registrationConfirmation";
		} else {
			return "Email is not registerd!";
		}
	}

	@Override
	public String resetPassword(String email, String password, String oldPassword) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(email);
		
		if(userEntity.isPresent()) {
			UserEntity userModel = userEntity.get(); 
			if(userModel.getPassword().equalsIgnoreCase(oldPassword)) {
				userModel.setPassword(passwordEncoder.encode(password));
				userRepository.save(userModel);
			
				return "Password Reset Successfully !";
			} else {
				return "Wrong Password !";
			}
		}
		
		return "Something went wrong !";
	}

	@Override
	public Map<String, Object> forgetPasswordAPI(String email) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		
		Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(email);

		if(userEntity.isPresent()) {
			UserEntity userData = userEntity.get();
			
			if(userData.getIsVerified().equals(Boolean.TRUE)) {
				
				emailService.sendPasswordResetMailToUser(userData);
				
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.FORGOT_PASSWORD_SUCCESS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
			} else {
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.LOGIN_NOT_VERIFIED);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			}
			
			
			return map;
		} else {
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.FORGOT_PASSWORD_EMAIL_NOT_EXISTS);
			map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			
			return map;
		}
	}

	@Override
	public Map<String, Object> resetPasswordAPI(String email, String password, String oldPassword) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		
		Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(email);
		
		if(userEntity.isPresent()) {
			UserEntity userModel = userEntity.get(); 
			if(userModel.getPassword().equalsIgnoreCase(oldPassword)) {
				userModel.setPassword(passwordEncoder.encode(password));
				userRepository.save(userModel);
			
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.RESET_PASSWORD_SUCCESS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			} else {
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.RESET_PASSWORD_WRONG_PASSWORD);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			}
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
		map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		
		return map;
	}

	@Override
	public Map<String, Object> verifyUserAPI(String email, String password) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(email);
		
		if(userEntity.isPresent()) {
			UserEntity userData = userEntity.get();
			
			if(userData.getPassword().equalsIgnoreCase(password)) {
				userData.setIsVerified(true);
				userRepository.save(userData);
				
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_VERIFICATION_SUCCESS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				
				return map;
			}
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_VERIFICATION_FAILURE);
		map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		
		return map;
	}

}