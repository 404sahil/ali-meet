package com.api.alimeet.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.api.alimeet.model.MeetingDto;
import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;

public interface MeetingService {

	public String saveMeeting(MeetingDto meetingDto);
	
	public Map<String, Object> saveMeetingAPI(MeetingDto meetingDto);
	
	public Map<String, Object> getUserByEmail(String emailId);
	
	public List<MeetingEntity> getMeetingByDate(Date meetingDate, Integer userId);
	
	public Map<String, Object> getMeetingByDateAPI(Date meetingDate, Integer userId, String searchText);
	
	public void sendMail(UserDto user);
	
	public String deleteMeeting(Long userId);
	
	public Map<String, Object> deleteMeetingAPI(Long userId);
	
	public String editMeeting(MeetingDto meetingDto);
	
	public Map<String, Object> editMeetingAPI(MeetingDto meetingDto);
	
	public String validateMeetingPassword(Long meetingId, String meetingPassword);
	
	public List<MeetingEntity> getAllMeetingListByDate(Date meetingDate);
	
	public MeetingEntity getByMeetingId(Long meetingId);
	
	public Map<String, Object> validateRoom(String roomName);
}
