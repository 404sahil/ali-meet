package com.api.alimeet.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.alimeet.model.UserEntity;
import com.api.alimeet.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	@PostMapping("/editUserProfile")
	public ResponseEntity<String> editUserProfile(@RequestBody UserEntity userEntity) {
		try {
			logger.info("editUserProfile : " + userEntity.getEmail());
			return new ResponseEntity<>(userService.editUserProfile(userEntity), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while editUserProfile {} :Reason :{}",
					userEntity.getEmail(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/editUser")
	public ResponseEntity<String> editUser(@RequestParam("userId") Long userId, 
			@RequestParam("userName") String userName, @RequestParam("password") String password,
			@RequestParam("role") String role) {
		try {
			logger.info("editUser : " + userId);
			return new ResponseEntity<>(userService.editUser(userId, userName, password, role), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while editUser {} :Reason :{}",
					userName,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/editUserAPI")
	public ResponseEntity<Map<String, Object>> editUserAPI(@RequestParam("userId") Long userId, 
			@RequestParam("userName") String userName, @RequestParam("password") String password,
			@RequestParam("role") String role) {
		try {
			logger.info("editUser : " + userId);
			return new ResponseEntity<>(userService.editUserAPI(userId, userName, password, role), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while editUserAPI {} :Reason :{}",
					userName,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getAllUsers/{role}")
	public ResponseEntity <Map<String, Object>> getAllUsers(@PathVariable("role") String role) {
		try {
			logger.info("Inside getAllUsers : " +role);
			
			return new ResponseEntity<>(userService.getAllUsers(role), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getAllUsers {} :Reason :{}",
					role,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getUserDataByEmail/{serchText}")
	public ResponseEntity <List<UserEntity>> searchByEmail(@PathVariable("serchText") String serchText) {
		try {
			logger.info("Inside searchByEmail : " +serchText);
			List<UserEntity> userEntity = userService.getUserDataByEmail(serchText);
			
			return new ResponseEntity<>(userEntity, HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getUserDataByEmail {} :Reason :{}",
					serchText,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/deleteUser")
	public ResponseEntity<String> deleteUser(@RequestParam(value = "userId") String userId) {
		try {
			logger.info("Inside deleteUser : " +userId);
			
			return new ResponseEntity<>(userService.deleteUser(Long.valueOf(userId)), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while deleteUser {} :Reason :{}",
					userId,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
}
