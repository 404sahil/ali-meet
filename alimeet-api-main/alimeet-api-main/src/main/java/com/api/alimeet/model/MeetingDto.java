package com.api.alimeet.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MeetingDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MeetingEntity meetingEntity;

	MeetingDto(){
		
	}
	public MeetingEntity getMeetingEntity() {
		return meetingEntity;
	}

	public void setMeetingEntity(MeetingEntity meetingEntity) {
		this.meetingEntity = meetingEntity;
	}

}
