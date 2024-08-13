package com.api.alimeet.service;

import java.util.List;
import java.util.Map;

import com.api.alimeet.model.AttendanceDto;
import com.api.alimeet.model.AttendanceEntity;

public interface AttendanceService {
	
	public AttendanceEntity addCheckInTime (Long meetingId, Long userId);
	
	public Map<String, Object> addCheckInTimeAPI (Long meetingId, Long userId);
	
	public String addCheckOutTime (Long attendanceId);
	
	public Map<String, Object> addCheckOutTimeAPI (Long attendanceId);
	
	public List<AttendanceDto> viewAttendance (Long meetingId);
	
	public Map<String, Object> viewAttendanceAPI (Long meetingId, Long userId);
	
	public List<AttendanceDto> viewAttendanceByUser (Long meetingId, Long userId);
	
	public Map<String, Object> viewAttendanceByUserAPI (Long meetingId, Long userId);
	
}
