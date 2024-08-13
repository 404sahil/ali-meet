package com.api.alimeet.service;

import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;

public interface EmailService {

	public void sendWelcomeMailToUser(UserDto userDto);
	
	public void sendPasswordResetMailToUser(UserEntity userDto);
	
	public void sendInvitationMailToUser(String email, Long meetingId, Long userId);
}
