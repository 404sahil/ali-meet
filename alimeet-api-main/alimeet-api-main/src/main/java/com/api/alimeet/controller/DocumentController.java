package com.api.alimeet.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.alimeet.model.DocumentEntity;
import com.api.alimeet.repository.DocumentRepository;
import com.api.alimeet.service.DocumentService;


@RestController
@RequestMapping("/document")
public class DocumentController {

	public static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	@Autowired
	DocumentService documentService;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@PostMapping(path = "/addDocument", consumes = "multipart/form-data")
	public ResponseEntity<String> addDocument(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "source") String source, 
			@RequestParam(value = "files") MultipartFile []files, 
			@RequestParam(value = "userId") Long userId ) {
		 
		return new ResponseEntity<>(documentService.addDocument(files, meetingId, source, userId), HttpStatus.OK);
	}
	
	@PostMapping("/getDocumentList")
	  public ResponseEntity<List<DocumentEntity>> 
		getDocumentList(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "source") String source, 
			@RequestParam(value = "userId") Long userId) {
		
		return new ResponseEntity<>(documentService.getDocumentList(meetingId, source, userId), HttpStatus.OK);
	}
	
	@PostMapping("/getDocumentListAPI")
	  public ResponseEntity<Map<String, Object>> 
		getDocumentListAPI(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "source") String source, 
			@RequestParam(value = "userId") Long userId) {
		
		return new ResponseEntity<>(documentService.getDocumentListAPI(meetingId, source, userId), HttpStatus.OK);
	}
	
	@PostMapping("/deleteDocument")
	  public ResponseEntity<List<DocumentEntity>> 
	deleteDocument(@RequestParam(value = "documentId") Long documentId) {
		
		return new ResponseEntity<>(documentService.deleteDocument(documentId), HttpStatus.OK);
	}
	
	@PostMapping("/deleteDocumentAPI")
	  public ResponseEntity<Map<String, Object>> 
	deleteDocumentAPI(@RequestParam(value = "documentId") Long documentId) {
		
		return new ResponseEntity<>(documentService.deleteDocumentAPI(documentId), HttpStatus.OK);
	}
	
	@PostMapping(path = "/addExcelData", consumes = "multipart/form-data")
	public ResponseEntity<String> addExcelData( 
			@RequestParam(value = "files") MultipartFile []files) {
		 
		return new ResponseEntity<>(documentService.addExcelData(files), HttpStatus.OK);
	}
	
	@PostMapping(path = "/addAllDocuments", consumes = "multipart/form-data")
	public ResponseEntity<Map<String, Object>> addAllDocuments(@RequestParam(value = "meetingId") Long meetingId, 
			@RequestParam(value = "source") String source, 
			@RequestParam(value = "files") MultipartFile []files, 
			@RequestParam(value = "userId") Long userId ) {
		 
		return new ResponseEntity<>(documentService.addAllDocuments(files, meetingId, source, userId), HttpStatus.OK);
	}
}
