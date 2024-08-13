package com.api.alimeet.serviceimpl;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.service.EmailService;

@Service("emailService")
@Transactional
public class EmailServiceImpl implements EmailService{
	
	public static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	@Autowired Session session;

	@Override
	public void sendWelcomeMailToUser(UserDto userDto) {
		// TODO Auto-generated method stub
		try {
			  System.out.println("userDto =======>"+userDto.getPassword());
		      // Create a default MimeMessage object.
		      MimeMessage message = new MimeMessage(session);

		      // Set To: header field of the header.
		      message.addRecipient(Message.RecipientType.TO, new InternetAddress(userDto.getEmail()));

		      // Set Subject: header field
		      message.setSubject("Welcome to AliMeet");

		      // HTML mail content
		      String htmlText =
			          "<html>\r\n"
			              + "<body>\r\n"
			              + "\r\n"
			              //+ "<p><b>Welcome to AliMeet</b></p>\r\n"
			             // + "\r\n"
			              //+ "<p>Please Verify your email. </P>\r\n"
			              + "<p>메일 인증 안내입니다.</p>\r\n"
			             // + "<p>Welcome!<br>The AliMeet Team</p>\r\n"
			             // + "\r\n"
			              + "<p>아래에 있는 을 눌러서 회원 가입 완료 해주세요. \r\n"
			              + "\r\n"
			              + "<p>감사합니다 .</p>\r\n"
			             // + "\r\n"
			              + "<a href=\"https://devmeet.alibiz.net/user/account/verifyaccount?token="+userDto.getPassword()+"&email="+userDto.getEmail()+"\">있는</a>"
			              + "</body>\r\n"
			              + "</html>";

		      // Now set the actual message
		      Multipart multipart = new MimeMultipart();
		      BodyPart msg = new MimeBodyPart();
		      msg.setDataHandler(
		          new DataHandler(new ByteArrayDataSource(htmlText, "text/html; charset=\"utf-8\"")));
		      multipart.addBodyPart(msg);
		      message.setContent(multipart);

		      // Send message
		      Transport.send(message);
		      logger.info("Mail sent successfully to newly created user {}", userDto.getEmail());
		    } catch (Exception e) {
		      // TODO : add pub-sub call
		     e.printStackTrace();
		    }
	}

	@Override
	public void sendPasswordResetMailToUser(UserEntity userDto) {
		// TODO Auto-generated method stub
		
		try {
			  System.out.println("userDto =======>"+userDto.getPassword());
		      // Create a default MimeMessage object.
		      MimeMessage message = new MimeMessage(session);

		      // Set To: header field of the header.
		      message.addRecipient(Message.RecipientType.TO, new InternetAddress(userDto.getEmail()));

		      // Set Subject: header field
		      message.setSubject("비밀번호를 재설정 - Alimeet");

		      // HTML mail content
		      String htmlText =
			          "<html>\r\n"
			              + "<body>\r\n"
			              + "\r\n"
			              + "<p><b>비밀번호를 재설정</b></p>\r\n"
			              + "\r\n"
			              + "<p>본 메일은 비밀번호 변경을 위해서 발송되는 메일입니다. 비밀번호 변경을 위해서. </P>\r\n"
			              + "\r\n"
			              + "<a href=\"https://devmeet.alibiz.net/resetpassword?token="+userDto.getPassword()+"&email="+userDto.getEmail()+"\">클릭</a>"
			              + "</body>\r\n"
			              + "</html>";

		      // Now set the actual message
		      Multipart multipart = new MimeMultipart();
		      BodyPart msg = new MimeBodyPart();
		      msg.setDataHandler(
		          new DataHandler(new ByteArrayDataSource(htmlText, "text/html; charset=\"utf-8\"")));
		      multipart.addBodyPart(msg);
		      message.setContent(multipart);

		      // Send message
		      Transport.send(message);
		      logger.info("Mail sent successfully to newly created user {}", userDto.getEmail());
		    } catch (Exception e) {
		      // TODO : add pub-sub call
		     e.printStackTrace();
		    }
		
	}

	@Override
	@Async
	public void sendInvitationMailToUser(String email, Long meetingId, Long userId) {
		try {
		      // Create a default MimeMessage object.
		      MimeMessage message = new MimeMessage(session);

		      // Set To: header field of the header.
		      message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

		      // Set Subject: header field
		      message.setSubject("Meeting Invitation - AliMeet");

		      // HTML mail content
		      String htmlText =
			          "<html>\r\n"
			              + "<body>\r\n"
			              + "\r\n"
			              //+ "<p><b>Welcome to AliMeet</b></p>\r\n"
			              + "<p>Please click below link to join the meeting.  \r\n"
			              + "\r\n"
			             // + "\r\n"
			              + "<a href=\"https://devmeet.alibiz.net?meetingId="+meetingId+"&userId="+userId+"&isRedirect="+true+"\">Join Meeting</a>"
			              + "</body>\r\n"
			              + "</html>";

		      // Now set the actual message
		      Multipart multipart = new MimeMultipart();
		      BodyPart msg = new MimeBodyPart();
		      msg.setDataHandler(
		          new DataHandler(new ByteArrayDataSource(htmlText, "text/html; charset=\"utf-8\"")));
		      multipart.addBodyPart(msg);
		      message.setContent(multipart);

		      // Send message
		      Transport.send(message);
		      logger.info("Mail sent successfully to invited user {}", email);
		    } catch (Exception e) {
		      // TODO : add pub-sub call
		     e.printStackTrace();
		    }
		
	}

}
