package com.api.alimeet.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.api.alimeet.model.DocumentEntity;

public interface DocumentService {
	
	public String addDocument (MultipartFile []files, Long meetingId, String source, Long userId);
	
	public String addExcelData (MultipartFile []files);
	
	public List<DocumentEntity> getDocumentList (Long meetingId, String source, Long userId);
	
	public Map<String, Object> getDocumentListAPI (Long meetingId, String source, Long userId);
	
	public List<DocumentEntity> deleteDocument (Long documentId);
	
	public Map<String, Object> deleteDocumentAPI (Long documentId);
	
	public Map<String, Object> addAllDocuments (MultipartFile []files, Long meetingId, String source, Long userId);
	
}
