package com.api.alimeet.constant;

public class ApplicationConstant {

	//Common
	public static final String SOMETING_WENT_WRONG = "문제가 발생했습니다!";
	
	//login
	public static final String LOGIN_SUCCESS = "API 응답 성공";
	public static final String LOGIN_INVALID_CREDENTIALS = "잘못된 자격 증명";
	public static final String LOGIN_NOT_VERIFIED = "계정이 확인되지 않았습니다.";
	
	//Registration
	public static final String REGISTRATION_SUCCESS = "등록 성공 !";
	public static final String REGISTRATION_EMAIL_EXISTS = "이메일이 이미 존재합니다.";
	
	//Forgot Password
	public static final String FORGOT_PASSWORD_SUCCESS = "비밀번호 분실 성공!";
	public static final String FORGOT_PASSWORD_EMAIL_NOT_EXISTS = "이메일이 등록되지 않았습니다!";
	
	//Reset Password
	public static final String RESET_PASSWORD_SUCCESS = "비밀번호 재설정 성공!";
	public static final String RESET_PASSWORD_WRONG_PASSWORD = "URL이 만료되었습니다! 재설정을 다시 시도하십시오.";
	
	//Response Status Code
	public static final String STATUS_200 = "200";
	public static final String STATUS_400 = "400";
	
	//Response Format
	public static final String RESPONSE_STATUS = "Status";
	public static final String RESPONSE_MESSAGE = "message";
	public static final String RESPONSE_DATA = "data";
	
	//Meeting
	public static final String MEETING_LIST_SUCCESS = "회의 목록 성공 !";
	public static final String MEETING_LIST_NOT_FOUND = "모임을 찾을 수 없습니다!";
	public static final String MEETING_SAVE_SUCCESS = "회의 저장 성공!";
	public static final String MEETING_DELETE_SUCCESS = "회의가 성공적으로 삭제되었습니다!";
	public static final String MEETING_ROOM_EXISTS = "회의실이 존재합니다!";
	public static final String MEETING_ROOM_NOT_EXISTS = "회의실이 존재하지 않습니다!";
	public static final String MEETING_EDIT_SUCCESS = "회의가 성공적으로 편집되었습니다!";
	public static final String MEETING_EDIT_FAILURE = "회의 편집에 문제가 있습니다. 다시 시도해 주세요!";
	
	//Document
	public static final String DOCUMENT_LIST_SUCCESS = "문서 목록 성공!";
	public static final String DOCUMENT_LIST_NOT_FOUND = "문서를 찾을 수 없습니다!";
	public static final String DOCUMENT_DELETE_SUCCESS = "문서가 성공적으로 삭제되었습니다!";
	public static final String DOCUMENT_NOT_FOUND = "문서를 찾을 수 없습니다!";
	public static final String DOCUMENT_SOURCE_PROFILE = "Profile";
	public static final String DOCUMENT_SOURCE_DOCUMENTS = "Documents";
	public static final String DOCUMENT_SOURCE_RECORDINGS = "Recordings";
	
	//Attendance
	public static final String ATTENDANCE_SUCCESS = "출석 목록 성공 !";
	public static final String ATTENDANCE_NOT_FOUND = "기록을 찾을 수 없습니다!";
	public static final String ATTENDANCE_CHECKOUT = "출석체크 성공!";
	
	//User
	public static final String USER_SUCCESS = "사용자 목록 성공!";
	public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다!";
	public static final String USER_VERIFICATION_SUCCESS = "사용자가 성공적으로 확인되었습니다!";
	public static final String USER_VERIFICATION_FAILURE = "사용자가 확인되지 않았습니다! 사용자를 다시 확인하십시오!";
	public static final String USER_EDIT_SUCCESS = "사용자가 성공적으로 편집되었습니다!";
	
	//AWS
	public static final String AWS_BUCKET_NAME = "devmeet-testing"; 
	
}
