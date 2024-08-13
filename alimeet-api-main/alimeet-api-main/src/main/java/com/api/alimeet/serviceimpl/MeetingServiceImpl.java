package com.api.alimeet.serviceimpl;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.api.alimeet.constant.ApplicationConstant;
import com.api.alimeet.model.DocumentEntity;
import com.api.alimeet.model.MeetingDto;
import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.MeetingInviteeEntity;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.AttendanceRepository;
import com.api.alimeet.repository.DocumentRepository;
import com.api.alimeet.repository.MeetingInviteeRepository;
import com.api.alimeet.repository.MeetingRepository;
import com.api.alimeet.repository.UserRepository;
import com.api.alimeet.service.EmailService;
import com.api.alimeet.service.MeetingService;

@Service
public class MeetingServiceImpl implements MeetingService {
	
	@Value("${alimeet.aws.accesskey}")
	private String awsAccessKey;

	@Value("${alimeet.aws.secretkey}")
	private String awsSecretKey;
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private MeetingInviteeRepository meetingInviteeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private AttendanceRepository attendanceRepository;

	@Override
	public String saveMeeting(MeetingDto meetingDto) {

		if (meetingDto != null) {
			
				MeetingEntity meetingEntity = populateMeetingData(meetingDto);
				MeetingEntity DbMeetingEntity = meetingRepository.save(meetingEntity);
				
				if(DbMeetingEntity != null) {
					for (String inv : meetingDto.getMeetingEntity().getInvites()) {
						Optional<UserEntity> user = userRepository.findOneByEmailIgnoreCase(inv);
						if(user.isPresent()) {
							UserEntity userEntity =user.get();
							emailService.sendInvitationMailToUser(userEntity.getEmail(), DbMeetingEntity.getMeetingId(), userEntity.getId());
						}
					}
					
				}
			
			return "Save Successfully";
		} else {
			return null;
		}

	}

	private MeetingEntity populateMeetingEntityData(MeetingEntity meetingEntityData) {
		MeetingEntity meetingEntity = new MeetingEntity();
		meetingEntity.setMeetingId(meetingEntityData.getMeetingId());
		meetingEntity.setRoomName(meetingEntityData.getRoomName());
		meetingEntity.setMeetingTitle(meetingEntityData.getMeetingTitle());
		meetingEntity.setUser(meetingEntityData.getUser());
		meetingEntity.setMeetingDesc(meetingEntityData.getMeetingDesc());
		meetingEntity.setStartTime(meetingEntityData.getStartTime());
		meetingEntity.setEndTime(meetingEntityData.getEndTime());
		meetingEntity.setCreatedOn(meetingEntityData.getCreatedOn());
		meetingEntity.setModifiedOn(meetingEntityData.getModifiedOn());
		meetingEntity.setStartMeetingTime(meetingEntityData.getStartMeetingTime());
		meetingEntity.setEndMeetingTime(meetingEntityData.getEndMeetingTime());
		
		return meetingEntity;
	}
	
	private MeetingEntity populateMeetingData(MeetingDto meetingDto) {
		MeetingEntity meetingEntity = new MeetingEntity();
		meetingEntity.setRoomName(meetingDto.getMeetingEntity().getRoomName());
		meetingEntity.setMeetingTitle(meetingDto.getMeetingEntity().getMeetingTitle());
		meetingEntity.setUser(meetingDto.getMeetingEntity().getUser());
		meetingEntity.setMeetingDesc(meetingDto.getMeetingEntity().getMeetingDesc());
		meetingEntity.setStartTime(meetingDto.getMeetingEntity().getStartTime());
		meetingEntity.setEndTime(meetingDto.getMeetingEntity().getEndTime());
		meetingEntity.setCreatedOn(LocalDateTime.now());
		meetingEntity.setModifiedOn(LocalDateTime.now());
		meetingEntity.setStartMeetingTime(meetingDto.getMeetingEntity().getStartMeetingTime());
		meetingEntity.setEndMeetingTime(meetingDto.getMeetingEntity().getEndMeetingTime());
		
		List<String> newList = meetingDto.getMeetingEntity().getInvites().stream()
                .distinct()
                .collect(Collectors.toList());
		
		for (String inv : newList) {
			Optional<UserEntity> user = userRepository.findOneByEmailIgnoreCase(inv);
			if(user.isPresent()) {
				UserEntity userEntity =user.get();
				
				MeetingInviteeEntity meetingInviteeEntity = new MeetingInviteeEntity();
				
				meetingInviteeEntity.setMeetingEntity(meetingEntity);
				meetingInviteeEntity.setInviteeId(userEntity.getId());
				meetingInviteeRepository.save(meetingInviteeEntity);
			}
		}
		
		return meetingEntity;
	}

	@Override
	public Map<String, Object> getUserByEmail(String emailId) {
		Map<String, Object> map = new HashMap<String, Object>();
		UserEntity userEntity = new UserEntity();
		Optional<UserEntity> userOptional = userRepository.findOneByEmailIgnoreCase(emailId);
		
		if(userOptional.isPresent()) {
			userEntity = userOptional.get();
		
			String docTitle = null;
			String url = null;
			
			List<DocumentEntity> documents = null;
			documents = documentRepository.findbyUserId(userEntity.getId(),"Profile");
			
			AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
			
			AmazonS3 s3client = AmazonS3ClientBuilder
					  .standard()
					  .withCredentials(new AWSStaticCredentialsProvider(credentials))
					  //.withResponseHeaders(new ResponseHeaderOverrides().withContentDisposition("attachment; filename*=UTF-8''\"A,B\""))
					  .withRegion(Regions.US_EAST_1)
					  .build();
			
			for (DocumentEntity documentEntity : documents) {
				docTitle = documentEntity.getDocumentTitle();
				url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,
						ApplicationConstant.DOCUMENT_SOURCE_PROFILE+"/"+userEntity.getId()+"/"+docTitle);
			}
			userEntity.setUrl(url);
			
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_SUCCESS);
			map.put(ApplicationConstant.RESPONSE_DATA, userEntity);
		} else {
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.USER_NOT_FOUND);
			map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		}
    	
	   
		return map;
		//return null;
		//return userEntity;
	}

	@Override
	public List<MeetingEntity> getMeetingByDate(Date meetingDate, Integer userId) {
		LocalDateTime  meetingdate = new java.sql.Timestamp(
	    		meetingDate.getTime()).toLocalDateTime();
		
		LocalDateTime  meetingStartdate =
		        LocalDateTime.of(meetingdate.getYear(), meetingdate.getMonth(), meetingdate.getDayOfMonth(), 00, 00);
		    
		    LocalDateTime meetingEnddate =
			        LocalDateTime.of(meetingdate.getYear(), meetingdate.getMonth(), meetingdate.getDayOfMonth(), 23, 59);
		
		    List<MeetingEntity> meetingEntity =
		    		meetingRepository.findByStartTimeBetweenIgnorecaEntities(meetingStartdate, meetingEnddate, userId.longValue());
		    
		    for (MeetingEntity meetingEntity2 : meetingEntity) {
		    	List<MeetingInviteeEntity> meetingInviteeEntities = 
		    			meetingInviteeRepository.findbyMeetingId(meetingEntity2.getMeetingId());
		    	
		    	List<String> inviteeEmail = new ArrayList<>();
		    	
		    	for (MeetingInviteeEntity meetingInviteeEntity : meetingInviteeEntities) {
		    		UserEntity userData = userRepository.getOne(meetingInviteeEntity.getInviteeId());
		    		inviteeEmail.add(userData.getEmail());
				}
		    	meetingEntity2.setInvites(inviteeEmail);
			}
		    
		    return meetingEntity;
	}

	@Override
	public void sendMail(UserDto user) {
		emailService.sendWelcomeMailToUser(user);
		
	}

	@Override
	public String deleteMeeting(Long meetingId) {
		// TODO Auto-generated method stub
		try {
			meetingInviteeRepository.deleteFromInvitee(meetingId);
			documentRepository.deleteFromDocument(meetingId);
			attendanceRepository.deleteFromAttendance(meetingId);
			meetingRepository.deleteFromMeeting(meetingId);
			return "Meeting Successfully Deleted !";
			
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String editMeeting(MeetingDto meetingDto) {
		// TODO Auto-generated method stub
		if(meetingDto != null && meetingDto.getMeetingEntity() != null 
				&& meetingDto.getMeetingEntity().getMeetingId() != null)
		{
		MeetingEntity meetingEntity = populateMeetingDataForEdit(meetingDto);
		meetingEntity.setMeetingId(meetingDto.getMeetingEntity().getMeetingId());
		meetingRepository.save(meetingEntity);
		}
		return null;
	}
	
	private MeetingEntity populateMeetingDataForEdit(MeetingDto meetingDto) {
		MeetingEntity meetingEntity = new MeetingEntity();
		meetingEntity.setRoomName(meetingDto.getMeetingEntity().getRoomName());
		meetingEntity.setMeetingTitle(meetingDto.getMeetingEntity().getMeetingTitle());
		meetingEntity.setUser(meetingDto.getMeetingEntity().getUser());
		meetingEntity.setMeetingDesc(meetingDto.getMeetingEntity().getMeetingDesc());
		meetingEntity.setStartTime(meetingDto.getMeetingEntity().getStartTime());
		meetingEntity.setEndTime(meetingDto.getMeetingEntity().getEndTime());
		meetingEntity.setCreatedOn(LocalDateTime.now());
		meetingEntity.setModifiedOn(LocalDateTime.now());
		meetingEntity.setMeetingPassword(meetingDto.getMeetingEntity().getMeetingPassword());
		
		if(meetingDto.getMeetingEntity().getMeetingId() != null) {
			meetingEntity.setMeetingId(meetingDto.getMeetingEntity().getMeetingId());
		}
		
		List<MeetingInviteeEntity> existingInviteeEntity = 
				meetingInviteeRepository.findbyMeetingId(meetingDto.getMeetingEntity().getMeetingId());
		
		meetingInviteeRepository.deleteAll(existingInviteeEntity);
		
		List<String> newList = meetingDto.getMeetingEntity().getInvites().stream()
                .distinct()
                .collect(Collectors.toList());

		for (String inv : newList) {
			System.out.println("Inside Loop : "+inv);
			Optional<UserEntity> user = userRepository.findOneByEmailIgnoreCase(inv);
			if(user.isPresent()) {
				System.out.println("Inside user.isPresent() :");
				UserEntity userEntity =user.get();
				
				MeetingInviteeEntity meetingInviteeEntity = new MeetingInviteeEntity();
				
				meetingInviteeEntity.setMeetingEntity(meetingEntity);
				meetingInviteeEntity.setInviteeId(userEntity.getId());
				//meetingInviteeRepository.save(meetingInviteeEntity);
				meetingInviteeRepository.addMeetingInvitee(meetingEntity.getMeetingId(),userEntity.getId());
				System.out.println("After Save : "+inv);
			}
		}
		
		return meetingEntity;
	}

	@Override
	public String validateMeetingPassword(Long meetingId, String meetingPassword) {
		// TODO Auto-generated method stub
		try {
			MeetingEntity meetingEntity = meetingRepository.getOne(meetingId);

			if(meetingEntity.getMeetingPassword() != null) {
				if(meetingPassword.equalsIgnoreCase(meetingEntity.getMeetingPassword())) {
					return "Password Matched!";
				}
			}
			
			return "Password Does not Matched!";
			
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public List<MeetingEntity> getAllMeetingListByDate(Date meetingDate) {
		// TODO Auto-generated method stub
		LocalDateTime  meetingdate = new java.sql.Timestamp(
	    		meetingDate.getTime()).toLocalDateTime();
		
		LocalDateTime  meetingStartdate =
		        LocalDateTime.of(meetingdate.getYear(), meetingdate.getMonth(), meetingdate.getDayOfMonth(), 00, 00);
		    
		    LocalDateTime meetingEnddate =
			        LocalDateTime.of(meetingdate.getYear(), meetingdate.getMonth(), meetingdate.getDayOfMonth(), 23, 59);
		
		    List<MeetingEntity> meetingEntity =
		    		meetingRepository.getAllMeetingList(meetingStartdate, meetingEnddate);
		    
		    for (MeetingEntity meetingEntity2 : meetingEntity) {
		    	List<MeetingInviteeEntity> meetingInviteeEntities = 
		    			meetingInviteeRepository.findbyMeetingId(meetingEntity2.getMeetingId());
		    	
		    	List<String> inviteeEmail = new ArrayList<>();
		    	
		    	for (MeetingInviteeEntity meetingInviteeEntity : meetingInviteeEntities) {
		    		UserEntity userData = userRepository.getOne(meetingInviteeEntity.getInviteeId());
		    		inviteeEmail.add(userData.getEmail());
				}
		    	meetingEntity2.setInvites(inviteeEmail);
			}
		    
		    return meetingEntity;
	}

	@Override
	public MeetingEntity getByMeetingId(Long meetingId) {
		// TODO Auto-generated method stub
		
		MeetingEntity meetingEntity = meetingRepository.getOne(meetingId);
		;
		return populateMeetingEntityData(meetingEntity);
	}

	String getSignedURL(AmazonS3 s3client, String bucketName, String objectKey) {
		
		try {

            // Set the presigned URL to expire after one hour.
			Date expiration = new Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withResponseHeaders(new ResponseHeaderOverrides().withContentDisposition("attachment; filename*=UTF-8''\"A,B\""))
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

            return url.toString();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
		
		return "";
	}

	@Override
	public Map<String, Object> getMeetingByDateAPI(Date meetingDate, Integer userId, String searchText) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		LocalDateTime  meetingdate = new java.sql.Timestamp(
	    		meetingDate.getTime()).toLocalDateTime();
		
		LocalDateTime  meetingStartdate =
		        LocalDateTime.of(meetingdate.getYear(), meetingdate.getMonth(), meetingdate.getDayOfMonth(), 00, 00);
		    
	    LocalDateTime meetingEnddate =
		        LocalDateTime.of(meetingdate.getYear(), meetingdate.getMonth(), meetingdate.getDayOfMonth(), 23, 59);
	
	    List<MeetingEntity> meetingEntity =
	    		meetingRepository.findByStartTimeBetweenIgnorecaEntities(meetingStartdate, meetingEnddate, userId.longValue());
	    
	    for (MeetingEntity meetingEntity2 : meetingEntity) {
	    	
    		System.out.println("in else");
    		List<MeetingInviteeEntity> meetingInviteeEntities = 
	    			meetingInviteeRepository.findbyMeetingId(meetingEntity2.getMeetingId());
	    	
	    	List<String> inviteeEmail = new ArrayList<>();
	    	
	    	for (MeetingInviteeEntity meetingInviteeEntity : meetingInviteeEntities) {
	    		UserEntity userData = userRepository.getOne(meetingInviteeEntity.getInviteeId());
	    		inviteeEmail.add(userData.getEmail());
			}
	    	meetingEntity2.setInvites(inviteeEmail);
	    	
		}
	    
	    map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
	    if(searchText != null) {
	    	List<MeetingEntity> entity = meetingEntity.stream()
	    		    .filter(contact -> contact.getMeetingTitle().toLowerCase().contains(searchText.toLowerCase()))
	    		    .collect(Collectors.toList());
	    	if(entity.size() > 0) {
	    		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_LIST_SUCCESS);
	    		map.put(ApplicationConstant.RESPONSE_DATA, entity);
	    	} else {
	    		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_LIST_NOT_FOUND);
	    		map.put(ApplicationConstant.RESPONSE_DATA, entity);
	    	}
	    } else {
	    	if(meetingEntity.size() > 0) {
	    		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_LIST_SUCCESS);
	    		map.put(ApplicationConstant.RESPONSE_DATA, meetingEntity);
	    	} else {
	    		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_LIST_NOT_FOUND);
	    		map.put(ApplicationConstant.RESPONSE_DATA, meetingEntity);
	    	}
	    }
	   
		return map;
		    
	}

	@Override
	public Map<String, Object> saveMeetingAPI(MeetingDto meetingDto) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> emails = new ArrayList<String>();
		
		if (meetingDto != null) {
			MeetingEntity meetingEntity = populateMeetingData(meetingDto);
			MeetingEntity DbMeetingEntity = meetingRepository.save(meetingEntity);
			
			if(DbMeetingEntity != null) {
				
				List<String> newList = meetingDto.getMeetingEntity().getInvites().stream()
                        .distinct()
                        .collect(Collectors.toList());
				
				for (String inv : newList) {
					Optional<UserEntity> user = userRepository.findOneByEmailIgnoreCase(inv);
					if(user.isPresent()) {
						UserEntity userEntity =user.get();
						emails.add(userEntity.getEmail());
						emailService.sendInvitationMailToUser(userEntity.getEmail(), 
								DbMeetingEntity.getMeetingId(), userEntity.getId());
					}
				}
				
			}
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_SAVE_SUCCESS);
			map.put(ApplicationConstant.RESPONSE_DATA, DbMeetingEntity);
			
			return map;
			
		} else {
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
			map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			
			return map;
		}
	}

	@Override
	public Map<String, Object> deleteMeetingAPI(Long meetingId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			meetingInviteeRepository.deleteFromInvitee(meetingId);
			documentRepository.deleteFromDocument(meetingId);
			attendanceRepository.deleteFromAttendance(meetingId);
			meetingRepository.deleteFromMeeting(meetingId);
			
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_DELETE_SUCCESS);
			map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			
			return map;
			
		} catch (Exception e) {
			
		}
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
		map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		
		return map;
	}

	@Override
	public Map<String, Object> validateRoom(String roomName) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapResponse = new HashMap<String, Object>();
		List<Long> mapResponse2 = new ArrayList<>();
		List<MeetingEntity> meetingEntity = new ArrayList<>();
		List<DocumentEntity> documents = null;
		
		meetingEntity = meetingRepository.findAll(hasRoomName(roomName));
		
		if(meetingEntity != null && meetingEntity.size() > 0) {
			
			for (MeetingEntity meetingEntity2 : meetingEntity) {
				List<MeetingInviteeEntity> existingInviteeEntity = 
						meetingInviteeRepository.findbyMeetingId(meetingEntity2.getMeetingId());
				mapResponse.put("meetingId", meetingEntity2.getMeetingId());
				mapResponse.put("meetingTitle",meetingEntity2.getMeetingTitle());
				
				documents = documentRepository.findbyMeetingId(meetingEntity2.getMeetingId(),"Documents");
				mapResponse.put("uploaded_documents_count",documents.size());
				
				System.out.println("meetingEntity2.getInvites() ===> "+meetingEntity2.getInvites());
				if(existingInviteeEntity != null && existingInviteeEntity.size() > 0) {
					for (MeetingInviteeEntity entity : existingInviteeEntity) {
						
						mapResponse2.add(entity.getInviteeId());
						
					} 
				}
				
				
				
				mapResponse.put("invitee", mapResponse2);
			}
			
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_ROOM_EXISTS);
			map.put(ApplicationConstant.RESPONSE_DATA, mapResponse);
			
		} else {
			map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_ROOM_NOT_EXISTS);
			map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		}
		
		return map;
	}
	
	static Specification<MeetingEntity> hasRoomName(String roomName) {
	    return (users, cq, cb) -> cb.or(cb.like(cb.lower(users.get("roomName")),
	        "%" + roomName.toLowerCase() + "%")
	    		);
	}

	@Override
	public Map<String, Object> editMeetingAPI(MeetingDto meetingDto) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			if(meetingDto != null && meetingDto.getMeetingEntity() != null 
					&& meetingDto.getMeetingEntity().getMeetingId() != null)
			{
				MeetingEntity meetingEntity = populateMeetingDataForEdit(meetingDto);
				meetingEntity.setMeetingId(meetingDto.getMeetingEntity().getMeetingId());
				meetingRepository.save(meetingEntity);
				
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_EDIT_SUCCESS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			} else {
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.MEETING_EDIT_FAILURE);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
			}
			
			return map;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.SOMETING_WENT_WRONG);
		map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		
		return map;
	}
}
