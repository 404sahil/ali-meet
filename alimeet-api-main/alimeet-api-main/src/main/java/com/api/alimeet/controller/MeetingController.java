package com.api.alimeet.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.api.alimeet.model.MeetingDto;
import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.MeetingRepository;
import com.api.alimeet.service.MeetingService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.JSONPObject;

@RestController
@RequestMapping("/meeting")
public class MeetingController{
	
	public static final Logger logger = LoggerFactory.getLogger(MeetingController.class);
	
	@Autowired
	MeetingService meetingService;
	
	@Autowired
	MeetingRepository meetingRepository;

	@PostMapping("/saveMeeting")
	public ResponseEntity<String> saveMeeting(@RequestBody MeetingDto meetingDto) {
		try {
			logger.info("meetingDto : " + meetingDto.getMeetingEntity().getUser());
			return new ResponseEntity<>(meetingService.saveMeeting(meetingDto), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while save meeting {} :Reason :{}",
					//meetingDto.getMeetingTitle(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/saveMeetingAPI")
	public ResponseEntity<Map<String, Object>> saveMeetingAPI(@RequestBody MeetingDto meetingDto) {
		try {
			logger.info("meetingDto : " + meetingDto.getMeetingEntity().getUser());
			return new ResponseEntity<>(meetingService.saveMeetingAPI(meetingDto), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while save meeting {} :Reason :{}",
					//meetingDto.getMeetingTitle(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getUserByEmail/{emailId}")
	public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable("emailId") String emailId) {
		try {
			logger.info("Inside getUserByEmail : " +emailId);
			return new ResponseEntity<>(meetingService.getUserByEmail(emailId), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getUserByEmail {} :Reason :{}",
					emailId,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getMeetingListByDate")
	public ResponseEntity<List<MeetingEntity>> getMeetingListByDate(@RequestParam(value = "meetingDate")
	@DateTimeFormat(pattern="yyyy-MM-dd") Date meetingDate, 
	@RequestParam(value = "userId") Integer userId) {
		try {
			logger.info("Inside getMeetingListByDate : " +meetingDate);
			return new ResponseEntity<>(meetingService.getMeetingByDate(meetingDate, userId), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getMeetingListByDate {} :Reason :{}",
					meetingDate,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/getMeetingListByDateAPI")
	public ResponseEntity<Map<String, Object>> getMeetingListByDateAPI(@RequestBody String userData) {
		Date meetingDate = null;
		try {
			JSONObject jsonObject = new JSONObject(userData);
			String search = null;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String meetingDateData = jsonObject.getString("meetingDate");
			meetingDate = dateFormat.parse(meetingDateData);
			
			Integer userId = jsonObject.getInt("userId");
			
			if (!jsonObject.isNull("search")) {
				search = jsonObject.getString("search");
			}
			
			logger.info("Inside getMeetingListByDateAPI : " +userData);
			return new ResponseEntity<>(meetingService.getMeetingByDateAPI(meetingDate, userId, search), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occured while getMeetingListByDateAPI {} :Reason :{}",
					meetingDate,
					e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/sendMail")
	public ResponseEntity<List<MeetingEntity>> sendMail(@RequestBody UserDto userDto) {
		try {
			logger.info("Inside sendMail : " +userDto.getEmail());
			meetingService.sendMail(userDto);
			return new ResponseEntity<>(null, HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getMeetingListByDate {} :Reason :{}",
					userDto.getEmail(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/deleteMeeting")
	public ResponseEntity<String> deleteMeeting(@RequestParam(value = "meetingId") Integer meetingId) {
		try {
			logger.info("Inside deleteMeeting userId : " +meetingId);
			return new ResponseEntity<>(meetingService.deleteMeeting(meetingId.longValue()), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while deleteMeeting userId {} :Reason :{}",
					meetingId,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/deleteMeetingAPI")
	public ResponseEntity<Map<String, Object>> deleteMeetingAPI(@RequestParam(value = "meetingId") Integer meetingId) {
		try {
			logger.info("Inside deleteMeetingAPI userId : " +meetingId);
			return new ResponseEntity<>(meetingService.deleteMeetingAPI(meetingId.longValue()), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while deleteMeetingAPI userId {} :Reason :{}",
					meetingId,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/editMeeting")
	public ResponseEntity<String> editMeeting(@RequestBody MeetingDto meetingDto) {
		try {
			logger.info("editMeeting : " + meetingDto.getMeetingEntity().getUser());
			return new ResponseEntity<>(meetingService.editMeeting(meetingDto), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while editMeeting meeting {} :Reason :{}",
					//meetingDto.getMeetingTitle(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/editMeetingAPI")
	public ResponseEntity<Map<String, Object>> editMeetingAPI(@RequestBody MeetingDto meetingDto) {
		try {
			logger.info("editMeetingAPI : " + meetingDto.getMeetingEntity().getUser());
			return new ResponseEntity<>(meetingService.editMeetingAPI(meetingDto), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while editMeetingAPI meeting {} :Reason :{}",
					meetingDto.getMeetingEntity().getMeetingTitle(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/addPassword")
	public ResponseEntity<String> addPassword(@RequestBody MeetingDto meetingDto) {
		try {
			logger.info("addPassword : " + meetingDto.getMeetingEntity().getUser());
			return new ResponseEntity<>(meetingService.editMeeting(meetingDto), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while editMeeting meeting {} :Reason :{}",
					//meetingDto.getMeetingTitle(),
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/validateMeetingPassword")
	public ResponseEntity<String> validateMeetingPassword(@RequestParam(value = "meetingId") Integer meetingId, 
			@RequestParam(value = "meetingPassword") String meetingPassword) {
		try {
			logger.info("Inside validateMeetingPassword meetingId : " +meetingId);
			return new ResponseEntity<>(meetingService.validateMeetingPassword(meetingId.longValue(),meetingPassword), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while deleteMeeting userId {} :Reason :{}",
					meetingId,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getAllMeetingListByDate")
	public ResponseEntity<List<MeetingEntity>> getAllMeetingListByDate(@RequestParam(value = "meetingDate")
	@DateTimeFormat(pattern="yyyy-MM-dd") Date meetingDate) {
		try {
			logger.info("Inside getAllMeetingListByDate : " +meetingDate);
			return new ResponseEntity<>(meetingService.getAllMeetingListByDate(meetingDate), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while getAllMeetingListByDate {} :Reason :{}",
					meetingDate,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/validateRoom")
	public ResponseEntity<Map<String, Object>> validateRoom(@RequestParam(value = "roomName") String roomName) {
		try {
			logger.info("Inside validateRoom userId : " +roomName);
			return new ResponseEntity<>(meetingService.validateRoom(roomName), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("Error occured while validateRoom roomaName {} :Reason :{}",
					roomName,
					e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
	}
	
}
