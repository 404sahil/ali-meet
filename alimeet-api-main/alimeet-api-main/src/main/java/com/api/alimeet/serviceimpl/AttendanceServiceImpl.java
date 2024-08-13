package com.api.alimeet.serviceimpl;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.alimeet.constant.ApplicationConstant;
import com.api.alimeet.model.AttendanceDto;
import com.api.alimeet.model.AttendanceEntity;
import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.AttendanceRepository;
import com.api.alimeet.repository.MeetingRepository;
import com.api.alimeet.repository.UserRepository;
import com.api.alimeet.service.AttendanceService;

@Service
public class AttendanceServiceImpl implements AttendanceService{
	
	public static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AttendanceRepository attendanceRepository;

	public AttendanceEntity addCheckInTime(Long meetingId, Long userId) {
		// TODO Auto-generated method stub
		logger.info("Method addCheckInTime ");
		try {
			
			AttendanceEntity attendanceEntity = new AttendanceEntity();
			MeetingEntity meetingFromDb = null;
			UserEntity userFromDb = null;
			
			Optional<MeetingEntity> meeting = meetingRepository.findById(meetingId);
			
		    if (meeting.isPresent()) {
		      meetingFromDb = meeting.get();
		    }
		    
		    
		    Optional<UserEntity> user = userRepository.findById(userId);
			
		    if (user.isPresent()) {
		    	userFromDb = user.get();
		    }
		    
		    attendanceEntity.setMeetingEntity(meetingFromDb);
		    attendanceEntity.setUserEntity(userFromDb);
		    attendanceEntity.setCheckIn(LocalDateTime.now());
		    attendanceEntity.setCheckOut(null);
		    attendanceEntity.setCreatedOn(LocalDateTime.now());
		    attendanceEntity.setModifiedOn(LocalDateTime.now());
			
		    AttendanceEntity entity =  attendanceRepository.save(attendanceEntity);
		    
		    return entity;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	    
	}

	@Override
	public String addCheckOutTime(Long attendanceId) {
		// TODO Auto-generated method stub
		
		AttendanceEntity attendanceFromDb = null;
		Optional<AttendanceEntity> attendance = attendanceRepository.findById(attendanceId);
		
	    if (attendance.isPresent()) {
	    	attendanceFromDb = attendance.get();
	    	attendanceFromDb.setCheckOut(LocalDateTime.now());
	    	
	    	attendanceRepository.save(attendanceFromDb);
	    	
	    	return "Attendance Check-out Successfully !";
	    }
		return null;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	@Override
	public List<AttendanceDto> viewAttendance(Long meetingId) {
		// TODO Auto-generated method stub
		
		List<AttendanceDto> attendanceReport = new ArrayList<AttendanceDto>();
		List<AttendanceEntity> attendance  = attendanceRepository.findbyMeetingId(meetingId);
		// Custom date format
		
		List<AttendanceEntity> distinctElements = attendance.stream().filter(distinctByKey(cust -> cust.getUserEntity().getId()))
				.collect(Collectors.toList());
		
		for (AttendanceEntity attendanceEntity : distinctElements) {
			System.out.println("attendanceEntity.getAttendanceId() ===> "+attendanceEntity.getAttendanceId());
		}
		
		for (AttendanceEntity attendanceEntity : distinctElements) {
			
			List<AttendanceEntity> attendanceByUser  = 
					attendanceRepository.findbyMeetingAndUserId(meetingId, attendanceEntity.getUserEntity().getId());
			
			Long totalAttendance = 0l;
			AttendanceDto dto = new AttendanceDto();

			for (AttendanceEntity attendanceUser : attendanceByUser) {
				if(attendanceUser.getCheckIn() != null && attendanceUser.getCheckOut() != null) {
					Duration duration = Duration.between(attendanceUser.getCheckIn(), 
							attendanceUser.getCheckOut());
					totalAttendance += duration.toMinutes();
				}
			}
			
			dto.setTotalTime(totalAttendance.toString()+" Minutes");
			dto.setCheckInTime(attendanceByUser.get(0).getCheckIn());
			dto.setCheckOutTime(attendanceByUser.get(attendanceByUser.size() - 1).getCheckOut());
			dto.setUserId(attendanceEntity.getUserEntity().getId().toString());
			
			UserEntity userEntity = attendanceEntity.getUserEntity();
			if(userEntity!=null) {
				dto.setUserName(userEntity.getUserName());
				dto.setEmail(userEntity.getEmail());
			}
			attendanceReport.add(dto);
		}
		return attendanceReport;
	}

	@Override
	public List<AttendanceDto> viewAttendanceByUser(Long meetingId, Long userId) {
		// TODO Auto-generated method stub
		List<AttendanceDto> attendanceReport = new ArrayList<AttendanceDto>();
		List<AttendanceEntity> attendance  = attendanceRepository.findbyMeetingAndUserId(meetingId, userId);
		
		for (AttendanceEntity attendanceEntity : attendance) {
			AttendanceDto dto = new AttendanceDto();
			if(attendanceEntity.getCheckIn() != null && attendanceEntity.getCheckOut() != null) {
				Duration duration = Duration.between(attendanceEntity.getCheckIn(), attendanceEntity.getCheckOut());
				dto.setTotalTime(Long.toString(duration.toMinutes())+" Minutes");
			}
			
			dto.setCheckInTime(attendanceEntity.getCheckIn());
			dto.setCheckOutTime(attendanceEntity.getCheckOut());
			
			UserEntity userEntity = attendanceEntity.getUserEntity();
			if(userEntity!=null) {
				dto.setUserName(userEntity.getUserName());
				dto.setEmail(userEntity.getEmail());
				dto.setUserId(userEntity.getId().toString());
			}
			attendanceReport.add(dto);
		}
		return attendanceReport;
	}

	@Override
	public Map<String, Object> addCheckInTimeAPI(Long meetingId, Long userId) {
		// TODO Auto-generated method stub
		logger.info("Method addCheckInTimeAPI - Start");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapRepsonse = new HashMap<String, Object>();
		try {
			
			AttendanceEntity attendanceEntity = new AttendanceEntity();
			MeetingEntity meetingFromDb = null;
			UserEntity userFromDb = null;
			
			Optional<MeetingEntity> meeting = meetingRepository.findById(meetingId);
			
		    if (meeting.isPresent()) {
		      meetingFromDb = meeting.get();
		    }
		    
		    Optional<UserEntity> user = userRepository.findById(userId);
			
		    if (user.isPresent()) {
		    	userFromDb = user.get();
		    }
		    
		    attendanceEntity.setMeetingEntity(meetingFromDb);
		    attendanceEntity.setUserEntity(userFromDb);
		    attendanceEntity.setCheckIn(LocalDateTime.now());
		    attendanceEntity.setCheckOut(null);
		    attendanceEntity.setCreatedOn(LocalDateTime.now());
		    attendanceEntity.setModifiedOn(LocalDateTime.now());
			
		    AttendanceEntity entity =  attendanceRepository.save(attendanceEntity);
		    
		    map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
		    
		    if(entity != null) {
		    	mapRepsonse.put("attendanceId", entity.getAttendanceId());
		    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_SUCCESS);
		    } else {
		    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_NOT_FOUND);
		    }
			
		    map.put(ApplicationConstant.RESPONSE_DATA, mapRepsonse);
		    
			return map;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, Object> addCheckOutTimeAPI(Long attendanceId) {
		// TODO Auto-generated method stub
		logger.info("Method addCheckOutTimeAPI - Start ");
		
		Map<String, Object> map = new HashMap<String, Object>();
		AttendanceEntity attendanceFromDb = null;
		
		try {
			Optional<AttendanceEntity> attendance = attendanceRepository.findById(attendanceId);
			
		    if (attendance.isPresent()) {
		    	attendanceFromDb = attendance.get();
		    	attendanceFromDb.setCheckOut(LocalDateTime.now());
		    	
		    	AttendanceEntity savedAttendance = attendanceRepository.save(attendanceFromDb);
		    	
		    	map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
			    map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			    
			    if(savedAttendance != null)
			    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_CHECKOUT);
			    else
			    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_NOT_FOUND);
			    
			    return map;
		    }
		} catch (Exception e) {
			logger.error("Exception in addCheckOutTimeAPI : "+e.getMessage());
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
	    map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
	    map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
		
		return map;
	}
	
	List<Object> getAttendanceData(Long meetingId, List<AttendanceEntity> attendance){
		
		List<Object> mapResponse2 = new ArrayList<>();
		
		List<AttendanceEntity> distinctElements = attendance.stream().filter(distinctByKey(cust -> cust.getUserEntity().getId()))
				.collect(Collectors.toList());
		
		for (AttendanceEntity attendanceEntity : distinctElements) {
			Map<String, Object> mapResponse = new HashMap<String, Object>();
			List<AttendanceEntity> attendanceByUser = null;
			
			
			if(meetingId != null && meetingId > 0) {
				attendanceByUser  = 
						attendanceRepository.findbyMeetingAndUserId(meetingId, 
							attendanceEntity.getUserEntity().getId());	
			} else {
				attendanceByUser  = 
						attendanceRepository.findbyOnlyUserId(
							attendanceEntity.getUserEntity().getId());
			}
				
			Long totalAttendance = 0l;
			for (AttendanceEntity attendanceUser : attendanceByUser) {
				if(attendanceUser.getCheckIn() != null && attendanceUser.getCheckOut() != null) {
					Duration duration = Duration.between(attendanceUser.getCheckIn(), 
							attendanceUser.getCheckOut());
					
					totalAttendance += duration.toMinutes();
				}
			}
			
			mapResponse.put("totalTime",totalAttendance.toString()+" Minutes");
			mapResponse.put("meetingTitle",attendanceEntity.getMeetingEntity().getMeetingTitle());
			mapResponse.put("userId", attendanceEntity.getUserEntity().getId().toString());
			mapResponse.put("checkInTime", attendanceByUser.get(0).getCheckIn());
			mapResponse.put("checkOutTime", attendanceByUser.get(attendanceByUser.size() - 1).getCheckOut());
			
			UserEntity userEntity = attendanceEntity.getUserEntity();
			if(userEntity!=null) {
				mapResponse.put("userName", userEntity.getUserName());
				mapResponse.put("email",userEntity.getEmail());
			}
			
			mapResponse2.add(mapResponse);
		}
		
		return mapResponse2;
	}

	public Map<String, Object> viewAttendanceAPI(Long meetingId, Long userId) {
		// TODO Auto-generated method stub
		logger.info("Method viewAttendanceAPI - Start ");
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> mapResponse2 = new ArrayList<>();
		List<AttendanceEntity> attendance = null;
		List<MeetingEntity> meetingList = null;
		
		try {
			
			if(meetingId != null && meetingId > 0) {
				attendance  = attendanceRepository.findbyMeetingId(meetingId);
				mapResponse2 = getAttendanceData(meetingId, attendance);
			} else {
				meetingList = meetingRepository.getMeetingListByUserId(userId);
				for (MeetingEntity meetingEntity : meetingList) {
					
					List<Object> mapResponse4 = new ArrayList<>();
					attendance  = attendanceRepository.findbyMeetingId(meetingEntity.getMeetingId());
					mapResponse4 = getAttendanceData(meetingEntity.getMeetingId(), attendance);
					
					if(mapResponse4.size() > 0) {
						System.out.println("meetingEntity.getMeetingId() ==> "+ meetingEntity.getMeetingId());
						mapResponse2.addAll(mapResponse4);
					}
					
				}
			}
				
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
		    map.put(ApplicationConstant.RESPONSE_DATA, mapResponse2);
		    
		    if(mapResponse2 != null && mapResponse2.size() > 0)
		    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_SUCCESS);
		    else
		    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_NOT_FOUND);
		    
		    return map;
			
			
		} catch (Exception e) {
			logger.error("Exception in viewAttendanceAPI : "+e.getMessage());
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
	    map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
	    map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
		
		return map;
	}

	@Override
	public Map<String, Object> viewAttendanceByUserAPI(Long meetingId, Long userId) {
		// TODO Auto-generated method stub
		logger.info("Method viewAttendanceByUserAPI - Start ");
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<AttendanceDto> attendanceReport = new ArrayList<AttendanceDto>();
		List<AttendanceEntity> attendance  = attendanceRepository.findbyMeetingAndUserId(meetingId, userId);
		
		try {
			
			for (AttendanceEntity attendanceEntity : attendance) {
				AttendanceDto dto = new AttendanceDto();
				if(attendanceEntity.getCheckIn() != null && attendanceEntity.getCheckOut() != null) {
					Duration duration = Duration.between(attendanceEntity.getCheckIn(), attendanceEntity.getCheckOut());
					dto.setTotalTime(Long.toString(duration.toMinutes())+" Minutes");
				}
				
				dto.setCheckInTime(attendanceEntity.getCheckIn());
				dto.setCheckOutTime(attendanceEntity.getCheckOut());
				
				UserEntity userEntity = attendanceEntity.getUserEntity();
				if(userEntity!=null) {
					dto.setUserName(userEntity.getUserName());
					dto.setEmail(userEntity.getEmail());
					dto.setUserId(userEntity.getId().toString());
				}
				
				attendanceReport.add(dto);
			}
			
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
		    map.put(ApplicationConstant.RESPONSE_DATA, attendanceReport);
		    
		    if(attendanceReport != null && attendanceReport.size() > 0)
		    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_SUCCESS);
		    else
		    	map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.ATTENDANCE_NOT_FOUND);
		    
		    return map;
		    
		} catch (Exception e) {
			logger.error("Exception in viewAttendanceByUserAPI : "+e.getMessage());
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
	    map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
	    map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
		
		return map;
	}
}
