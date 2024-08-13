package com.api.alimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.api.alimeet.model.AttendanceEntity;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

	//List<DocumentEntity> findByMeetingMeetingId(Long meetingId);
	
	@Query("SELECT m FROM AttendanceEntity m WHERE m.meetingEntity.meetingId = :meetingId")
	List<AttendanceEntity> findbyMeetingId(Long meetingId);
	
	@Query("SELECT m FROM AttendanceEntity m WHERE m.meetingEntity.meetingId = :meetingId GROUP BY m.userEntity.id")
	List<AttendanceEntity> findbyMeetingIdUniqueUser(Long meetingId);
	
	@Query("SELECT m FROM AttendanceEntity m WHERE m.meetingEntity.meetingId = :meetingId and m.userEntity.id = :userId")
	List<AttendanceEntity> findbyMeetingAndUserId(Long meetingId, Long userId);
	
	@Query("SELECT m FROM AttendanceEntity m WHERE m.userEntity.id = :userId")
	List<AttendanceEntity> findbyOnlyUserId(Long userId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM AttendanceEntity d WHERE d.meetingEntity.meetingId = :meetingId")
	void deleteFromAttendance(Long meetingId);
	
	@Query("SELECT m FROM DocumentEntity m WHERE m.userId = :userId and m.documentSource = :source")
	List<AttendanceEntity> findbyUserId(Long userId, String source);
}
