package com.api.alimeet.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.alimeet.model.MeetingEntity;

@Repository
public interface MeetingRepository extends JpaRepository<MeetingEntity, Long>, JpaSpecificationExecutor<MeetingEntity> {

	@Query("SELECT DISTINCT m FROM MeetingEntity m inner join MeetingInviteeEntity e "
			+ "on m.meetingId=e.meetingEntity "
			+ "WHERE m.startTime BETWEEN :meetingStartDate AND :meetingEndDate AND e.inviteeId = :userId")
	List<MeetingEntity> findByStartTimeBetweenIgnorecaEntities(
			@Param("meetingStartDate") LocalDateTime meetingStartDate,
			@Param("meetingEndDate") LocalDateTime meetingEndDate, @Param("userId") Long userId);
	
	@Query("SELECT DISTINCT m FROM MeetingEntity m inner join MeetingInviteeEntity e "
			+ "on m.meetingId=e.meetingEntity "
			+ "WHERE m.startTime BETWEEN :meetingStartDate AND :meetingEndDate")
	List<MeetingEntity> getAllMeetingList(
			@Param("meetingStartDate") LocalDateTime meetingStartDate,
			@Param("meetingEndDate") LocalDateTime meetingEndDate);
	
	@Query("SELECT DISTINCT m FROM MeetingEntity m where m.user.id = :userId")
	List<MeetingEntity> getMeetingListByUserId(@Param("userId") Long userId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM MeetingEntity m WHERE m.meetingId = :id")
	void deleteFromMeeting(Long id);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM MeetingEntity m WHERE m.user.id = :id")
	void deleteUserFromMeeting(Long id);
}
