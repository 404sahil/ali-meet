package com.api.alimeet.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.alimeet.model.AttendanceDto;
import com.api.alimeet.model.AttendanceEntity;
import com.api.alimeet.service.AttendanceService;


@RestController
@RequestMapping("/attendance")
public class AttendanceController {

	public static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);
	
	@Autowired
	AttendanceService attendanceService;
	
	
	@PostMapping(path = "/addCheckInTime")
	public ResponseEntity<AttendanceEntity> addCheckInTime(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "userId") Long userId ) {
		 
		return new ResponseEntity<>(attendanceService.addCheckInTime(meetingId, userId), HttpStatus.OK);
	}
	
	@PostMapping(path = "/addCheckInTimeAPI")
	public ResponseEntity<Map<String, Object>> addCheckInTimeAPI(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "userId") Long userId ) {
		 
		return new ResponseEntity<>(attendanceService.addCheckInTimeAPI(meetingId, userId), HttpStatus.OK);
	}
	
	@PostMapping(path = "/addCheckOutTime")
	public ResponseEntity<String> addCheckOutTime(@RequestParam(value = "attendanceId") Long attendanceId) {
		 
		return new ResponseEntity<>(attendanceService.addCheckOutTime(attendanceId), HttpStatus.OK);
	}
	
	@PostMapping(path = "/addCheckOutTimeAPI")
	public ResponseEntity<Map<String, Object>> addCheckOutTimeAPI(@RequestParam(value = "attendanceId") Long attendanceId) {
		 
		return new ResponseEntity<>(attendanceService.addCheckOutTimeAPI(attendanceId), HttpStatus.OK);
	}
	
	@GetMapping(path = "/viewAttendance")
	public ResponseEntity<List<AttendanceDto>> viewAttendance(@RequestParam(value = "meetingId") Long meetingId) {
		 
		return new ResponseEntity<List<AttendanceDto>>(attendanceService.viewAttendance(meetingId), HttpStatus.OK);
	}
	
	@GetMapping(path = "/viewAttendanceAPI")
	public ResponseEntity<Map<String, Object>> viewAttendanceAPI(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "userId") Long userId) {
		 
		return new ResponseEntity<>(attendanceService.viewAttendanceAPI(meetingId, userId), HttpStatus.OK);
	}
	
	@GetMapping(path = "/viewAttendanceByUser")
	public ResponseEntity<List<AttendanceDto>> viewAttendanceByUser(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "userId") Long userId) {
		 
		return new ResponseEntity<List<AttendanceDto>>(attendanceService.viewAttendanceByUser(meetingId, userId), HttpStatus.OK);
	}
	
	@GetMapping(path = "/viewAttendanceByUserAPI")
	public ResponseEntity<Map<String, Object>> viewAttendanceByUserAPI(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "userId") Long userId) {
		 
		return new ResponseEntity<>(attendanceService.viewAttendanceByUserAPI(meetingId, userId), HttpStatus.OK);
	}
	
}
