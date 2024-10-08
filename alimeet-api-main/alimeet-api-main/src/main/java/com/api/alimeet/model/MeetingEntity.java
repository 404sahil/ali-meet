package com.api.alimeet.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
//@JsonIdentityInfo(
//	    scope = Meeting.class,
//	    generator = ObjectIdGenerators.PropertyGenerator.class,
//	    property = "meetingId",
//	    resolver = DuplicateObjectIdResolver.class)
public class MeetingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long meetingId;

	@Column(nullable = false)
	private String roomName;

	@Column(nullable = false)
	private String meetingTitle;

	@Column(nullable = false)
	private String meetingDesc;

	@Transient
	private List<String> invites;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;
	
	@Column
	private String startMeetingTime;

	@Column
	private String endMeetingTime;

	@CreatedDate
	@Column
	private LocalDateTime createdOn;

	@LastModifiedDate
	@Column
	private LocalDateTime modifiedOn;

	@ManyToOne()
	@JoinColumn(name = "user_id")
	private UserEntity user = new UserEntity();
	
	@Column
	private String meetingPassword;

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getMeetingTitle() {
		return meetingTitle;
	}

	public void setMeetingTitle(String meetingTitle) {
		this.meetingTitle = meetingTitle;
	}

	public String getMeetingDesc() {
		return meetingDesc;
	}

	public void setMeetingDesc(String meetingDesc) {
		this.meetingDesc = meetingDesc;
	}

	public List<String> getInvites() {
		return invites;
	}

	public void setInvites(List<String> invites) {
		this.invites = invites;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getMeetingPassword() {
		return meetingPassword;
	}

	public void setMeetingPassword(String meetingPassword) {
		this.meetingPassword = meetingPassword;
	}

	public String getStartMeetingTime() {
		return startMeetingTime;
	}

	public void setStartMeetingTime(String startMeetingTime) {
		this.startMeetingTime = startMeetingTime;
	}

	public String getEndMeetingTime() {
		return endMeetingTime;
	}

	public void setEndMeetingTime(String endMeetingTime) {
		this.endMeetingTime = endMeetingTime;
	}

}
