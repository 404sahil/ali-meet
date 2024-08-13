package com.api.alimeet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.api.alimeet.model.DocumentEntity;
import com.api.alimeet.model.MeetingInviteeEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

	//List<DocumentEntity> findByMeetingMeetingId(Long meetingId);
	
	@Query("SELECT m FROM DocumentEntity m WHERE m.meetingEntity.meetingId = :meetingId and m.documentSource = :source")
	List<DocumentEntity> findbyMeetingId(Long meetingId, String source);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM DocumentEntity d WHERE d.meetingEntity.meetingId = :meetingId")
	void deleteFromDocument(Long meetingId);
	
	@Query("SELECT m FROM DocumentEntity m WHERE m.userId = :userId and m.documentSource = :source")
	List<DocumentEntity> findbyUserId(Long userId, String source);
}
