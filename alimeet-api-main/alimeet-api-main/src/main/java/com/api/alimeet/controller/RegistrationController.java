package com.api.alimeet.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.service.EmailService;
import com.api.alimeet.service.MeetingService;
import com.api.alimeet.service.RegistrationService;

@RestController
@RequestMapping("/register")
public class RegistrationController {

	public static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	RegistrationService registrationService;

	@Autowired
	EmailService emailService;
	
	@Autowired
	MeetingService meetingService;

	@GetMapping("/register")
	public String getRegistrationView() {

		return "registration";
	}

	@PostMapping("/registerUser")
	public ResponseEntity<Map<String, Object>> userRegistration(@RequestBody UserDto user,
			@RequestParam(value = "action") String action) {
		try {

			return new ResponseEntity<>(registrationService.saveUser(user, action), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}", user.getEmail(), e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/verifyUser")
	public ResponseEntity<String> verifyUser(@RequestParam(value = "email") String email, 
			@RequestParam(value = "password") String password) {
		try {

			return new ResponseEntity<>(registrationService.verifyUser(email, password), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/verifyUserAPI")
	public ResponseEntity<String> verifyUserAPI(@RequestParam(value = "email") String email, 
			@RequestParam(value = "password") String password) {
		try {

			return new ResponseEntity<>(registrationService.verifyUser(email, password), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/forgetPassword")
	public ResponseEntity<String> forgetPassword(@RequestParam(value = "email") String email) {
		try {

			return new ResponseEntity<>(registrationService.forgetPassword(email), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/forgetPasswordAPI")
	public ResponseEntity<Map<String, Object>> forgetPasswordAPI(@RequestParam(value = "email") String email) {
		try {

			return new ResponseEntity<>(registrationService.forgetPasswordAPI(email), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestParam(value = "email") String email, 
			@RequestParam(value = "password") String password, 
			@RequestParam(value = "oldPassword") String oldPassword) {
		try {

			return new ResponseEntity<>(registrationService.resetPassword(email, password, oldPassword), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/resetPasswordAPI")
	public ResponseEntity<Map<String, Object>> resetPasswordAPI(@RequestParam(value = "email") String email, 
			@RequestParam(value = "password") String password, 
			@RequestParam(value = "oldPassword") String oldPassword) {
		try {

			return new ResponseEntity<>(registrationService.resetPasswordAPI(email, password, oldPassword), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occured while registering user {} :Reason :{}");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getMeetingById")
	public ResponseEntity<MeetingEntity> getMeetingById(@RequestParam(value = "meetingId") Long meetingId) {
		try {
			logger.info("Inside getMeetingById : " +meetingId);
			
			return new ResponseEntity<>(meetingService.getByMeetingId(meetingId), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getMeetingById {} :Reason :{}",
					meetingId,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
