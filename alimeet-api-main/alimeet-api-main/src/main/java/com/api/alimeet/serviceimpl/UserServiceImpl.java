package com.api.alimeet.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.alimeet.constant.ApplicationConstant;
import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.MeetingInviteeEntity;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.DocumentRepository;
import com.api.alimeet.repository.MeetingInviteeRepository;
import com.api.alimeet.repository.MeetingRepository;
import com.api.alimeet.repository.UserRepository;
import com.api.alimeet.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MeetingInviteeRepository meetingInviteeRepository;
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;


	@Override
	public String editUserProfile(UserEntity userEntity) {
		// TODO Auto-generated method stub
		userRepository.save(userEntity);
		return "User Edited Successfully !";
	}

	@Override
	public Map<String, Object> getAllUsers(String role) {
		// TODO Auto-generated method stub
		List<UserEntity> userDetails = new ArrayList<>();
		List<UserDto> userDtoDetails = new ArrayList<>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(!role.equalsIgnoreCase("both")) {
			userDetails = userRepository.findByRole(role);
		}else {
			userDetails = userRepository.findAll(hasRole("Teacher", "Student"));
		}
		
		for (UserEntity userEntity : userDetails) {
			UserDto userDtoDetail = new UserDto();
			
			if(userEntity.getIsVerified() == Boolean.TRUE) {
				userDtoDetail.setId(userEntity.getId().toString());
				userDtoDetail.setEmail(userEntity.getEmail());
				userDtoDetail.setUserName(userEntity.getUserName());
				
				userDtoDetails.add(userDtoDetail);
			}
			
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
		if(userDtoDetails.size() > 0)
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_SUCCESS);
		else
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_NOT_FOUND);
    	map.put(ApplicationConstant.RESPONSE_DATA, userDtoDetails);
	   
		return map;
	}

	@Override
	public List<UserEntity> getUserDataByEmail(String searchText) {
		// TODO Auto-generated method stub
		return userRepository.findAll(hasEmail(searchText));
	}
	
	static Specification<UserEntity> hasEmail(String email) {
	    return (users, cq, cb) -> cb.or(cb.like(cb.lower(users.get("email")),
	        "%" + email.toLowerCase() + "%"), 
	    		cb.like(cb.lower(users.get("userName")),
	    		        "%" + email.toLowerCase() + "%")
	    		);
	}
	
	static Specification<UserEntity> hasRole(String role1, String role2) {
	    return (users, cq, cb) -> cb.or(cb.like(cb.lower(users.get("role")),
	        "%" + role1.toLowerCase() + "%"), 
	    		cb.like(cb.lower(users.get("role")),
	    		        "%" + role2.toLowerCase() + "%")
	    		);
	}

	@Override
	public String deleteUser(Long userId) {
		// TODO Auto-generated method stub
		
		try {
			
			List<MeetingInviteeEntity> entities = 
					meetingInviteeRepository.findbyInvaiteeId(userId);
			
			meetingInviteeRepository.deleteUserFromInvitee(userId);
			
			for (MeetingInviteeEntity meetingInviteeEntity : entities) {
				System.out.println("meetingInviteeEntity.getMeetingEntity().getMeetingId() ======. "+meetingInviteeEntity.getMeetingEntity().getMeetingId());
				documentRepository.deleteFromDocument(meetingInviteeEntity.getMeetingEntity().getMeetingId());
			}
			meetingRepository.deleteUserFromMeeting(userId);
			userRepository.deleteById(userId);
			
			return "Meeting Successfully Deleted !";
			
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String editUser(Long userId, String userName, String password, String role) {
		// TODO Auto-generated method stub
		
		Optional<UserEntity> userEntity = userRepository.findById(userId);
		UserEntity user = userEntity.get();
		
		user.setUserName(userName);
		user.setRole(role);
		
		if(!user.getPassword().equalsIgnoreCase(password)) {
			user.setPassword(passwordEncoder.encode(password));
		}
		
		userRepository.save(user);
		
		return "success";
	}

	@Override
	public Map<String, Object> editUserAPI(Long userId, String userName, String password, String role) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userEntity = userRepository.findById(userId);
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			
			if(userEntity != null) {
				UserEntity user = userEntity.get();
				
				user.setUserName(userName);
				user.setRole(role);
				
				if(!user.getPassword().equalsIgnoreCase(password)) {
					user.setPassword(passwordEncoder.encode(password));
				}
				
				userRepository.save(user);
				
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_EDIT_SUCCESS);
		    	map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			}
		} catch (Exception e) {
			// TODO: handle exception
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
	    	map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		}
		
		return map;
	}

}
