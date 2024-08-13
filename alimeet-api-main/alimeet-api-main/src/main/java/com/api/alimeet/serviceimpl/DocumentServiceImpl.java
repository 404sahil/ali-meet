package com.api.alimeet.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.api.alimeet.constant.ApplicationConstant;
import com.api.alimeet.model.DocumentEntity;
import com.api.alimeet.model.MeetingEntity;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.repository.DocumentRepository;
import com.api.alimeet.repository.MeetingInviteeRepository;
import com.api.alimeet.repository.MeetingRepository;
import com.api.alimeet.repository.UserRepository;
import com.api.alimeet.service.DocumentService;
import com.api.alimeet.service.RegistrationService;

@Service
public class DocumentServiceImpl implements DocumentService{
	
	public static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	
	@Value("${alimeet.aws.accesskey}")
	private String awsAccessKey;

	@Value("${alimeet.aws.secretkey}")
	private String awsSecretKey;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MeetingInviteeRepository meetingInviteeRepository;
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	DocumentService documentService;
	
	@Override
	public String addDocument(MultipartFile[] files, Long meetingId, String source, Long userId) {
		// TODO Auto-generated method stub
		
		MeetingEntity meetingFromDb = null;
		
		if(!source.equalsIgnoreCase("Profile")) {
			Optional<MeetingEntity> meeting = meetingRepository.findById(meetingId);
			
		    if (meeting.isPresent()) {
		      meetingFromDb = meeting.get();
		    }
		}
		
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_EAST_1)
				  .build();
		
		for (MultipartFile file : files) {
			
			DocumentEntity document = new DocumentEntity();
			
			File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
			try {
				file.transferTo(convFile);
				
				if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
					s3client.putObject(
							  ApplicationConstant.AWS_BUCKET_NAME, 
							  meetingId.toString()+"/"+source+"/"+file.getOriginalFilename(), 
							  convFile
							);
				} else {
					s3client.putObject(
							  ApplicationConstant.AWS_BUCKET_NAME, 
							  source+"/"+userId.toString()+"/"+file.getOriginalFilename(), 
							  convFile
							);
				}
				
				document.setDocumentTitle(file.getOriginalFilename());
				document.setMeetingEntity(meetingFromDb);
				document.setCreatedOn(LocalDateTime.now());
				document.setModifiedOn(LocalDateTime.now());
				document.setDocumentSource(source);
				document.setUserId(userId);
				
				documentRepository.save(document);
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		return "Document Added Successfully!";
	}

	public List<DocumentEntity> getDocumentList(Long meetingId, String source, Long userId) {
		// TODO Auto-generated method stub
		
		List<DocumentEntity> documents = null;
		
		if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
			documents = 
					documentRepository.findbyMeetingId(meetingId,source);
		} else {
			documents = 
					documentRepository.findbyUserId(userId,source);
		}
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  //.withResponseHeaders(new ResponseHeaderOverrides().withContentDisposition("attachment; filename*=UTF-8''\"A,B\""))
				  .withRegion(Regions.US_EAST_1)
				  .build();
		
		if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
			for (DocumentEntity documentEntity : documents) {
				String docTitle = documentEntity.getDocumentTitle();
				String url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,
						meetingId+"/"+source+"/"+docTitle);
				documentEntity.setUrl(url);
			}
		} else {
			for (DocumentEntity documentEntity : documents) {
				String docTitle = documentEntity.getDocumentTitle();
				String url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,
						source+"/"+userId+"/"+docTitle);
				documentEntity.setUrl(url);
			}
		}
		
		return documents;
	}


	String getSignedURL(AmazonS3 s3client, String bucketName, String objectKey) {
		
		try {

            // Set the presigned URL to expire after one hour.
			Date expiration = new Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withResponseHeaders(new ResponseHeaderOverrides().withContentDisposition("attachment; filename*=UTF-8''\"A,B\""))
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

            return url.toString();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
		
		return "";
	}
	
	String getDocumentUrl(DocumentEntity document, Long meetingId, String source, Long userId) {
		
		String url = null;
		
		try {
			
			AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
			
			AmazonS3 s3client = AmazonS3ClientBuilder
					  .standard()
					  .withCredentials(new AWSStaticCredentialsProvider(credentials))
					  .withRegion(Regions.US_EAST_1)
					  .build();
			
			if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
				String docTitle = document.getDocumentTitle();
				url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,meetingId+"/"+source+"/"+docTitle);
				document.setUrl(url);
			} else {
				String docTitle = document.getDocumentTitle();
				url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,source+"/"+userId+"/"+docTitle);
				document.setUrl(url);
			}
			
			return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return url;
	}

	@Override
	public String addExcelData(MultipartFile[] files) {
		// TODO Auto-generated method stub
		
		try
        {
			for (MultipartFile file : files) {
				File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
				
				file.transferTo(convFile);
	            FileInputStream fileExcel = new FileInputStream(convFile);
	            
	            Integer rawCount = 0;
	            Integer columnCount = 0;
	 
	            //Create Workbook instance holding reference to .xlsx file
	            XSSFWorkbook workbook = new XSSFWorkbook(fileExcel);
	 
	            //Get first/desired sheet from the workbook
	            XSSFSheet sheet = workbook.getSheetAt(0);
	 
	            //Iterate through each rows one by one
	            Iterator<Row> rowIterator = sheet.iterator();
		            while (rowIterator.hasNext()) 
		            {
		            	UserDto userData = new UserDto();
		                Row row = rowIterator.next();
		                //For each row, iterate through all the columns
		                Iterator<Cell> cellIterator = row.cellIterator();
		                if(rawCount >= 1) {
			                while (cellIterator.hasNext()) 
			                {
			                    Cell cell = cellIterator.next();
			                    //Check the cell type and format accordingly
			                    if(cell.getCellType().toString().equalsIgnoreCase("NUMERIC")) {
			                    	System.out.print(cell.getNumericCellValue() + " rawCount : "+rawCount +" And columnCount : "+columnCount );
			                    	if(columnCount == 0)
			                    		userData.setUserName(Double.toString(cell.getNumericCellValue()));
			                    	if(columnCount == 1)
			                    		userData.setRole(Double.toString(cell.getNumericCellValue()));
			                    	if(columnCount == 2)
			                    		userData.setEmail(Double.toString(cell.getNumericCellValue()));
			                    	if(columnCount == 3)
			                    		userData.setPassword(Double.toString(cell.getNumericCellValue()));
			                    } else if(cell.getCellType().toString().equalsIgnoreCase("STRING")) {
			                    	System.out.print(cell.getStringCellValue() + " rawCount : "+rawCount +" And columnCount : "+columnCount);
			                    	
			                    	if(columnCount == 0)
			                    		userData.setUserName(cell.getStringCellValue());
			                    	if(columnCount == 1)
			                    		userData.setRole(cell.getStringCellValue());
			                    	if(columnCount == 2)
			                    		userData.setEmail(cell.getStringCellValue());
			                    	if(columnCount == 3)
			                    		userData.setPassword(cell.getStringCellValue());
			                    }
		
			                   ++columnCount;
			                }
			                registrationService.saveUser(userData, "addByAdmin");
		                }
		                
		                columnCount = 0;
		                ++rawCount;
		                System.out.println("");
		            }
	            fileExcel.close();
	       		}
        	}
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
		return null;
	}

	@Override
	public List<DocumentEntity> deleteDocument(Long documentId) {
		
		Optional<DocumentEntity> documents = null;
		
		documents = 
				documentRepository.findById(documentId);
		
		  if (documents.isPresent()) {
			  
			  DocumentEntity documentEntity = documents.get();
			  
			  if(documentEntity.getDocumentSource().equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_RECORDINGS) || 
					  documentEntity.getDocumentSource().equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_DOCUMENTS)) {
				  AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
					
				  AmazonS3 s3client = AmazonS3ClientBuilder
						  .standard()
						  .withCredentials(new AWSStaticCredentialsProvider(credentials))
						  .withRegion(Regions.US_EAST_1)
						  .build();
					
				  s3client.deleteObject(ApplicationConstant.AWS_BUCKET_NAME,documentEntity.getMeetingEntity().getMeetingId()+"/"+documentEntity.getDocumentSource()+"/"+documentEntity.getDocumentTitle());
				  
				  documentRepository.deleteById(documentId);
				  
				  return documentService.getDocumentList(documentEntity.getMeetingEntity().getMeetingId(),documentEntity.getDocumentSource() , null);
			  }
		  }
		
		return null;
	}

	@Override
	public Map<String, Object> addAllDocuments(MultipartFile[] files, Long meetingId, String source, Long userId) {
		MeetingEntity meetingFromDb = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentEntity> savedDocuments = new ArrayList<DocumentEntity>();
		
		if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
			Optional<MeetingEntity> meeting = meetingRepository.findById(meetingId);
			
		    if (meeting.isPresent()) {
		      meetingFromDb = meeting.get();
		    }
		}
		
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_EAST_1)
				  .build();
		
		for (MultipartFile file : files) {
			
			DocumentEntity document = new DocumentEntity();
			
			File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
			try {
				file.transferTo(convFile);
				
				if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
					s3client.putObject(
							  ApplicationConstant.AWS_BUCKET_NAME, 
							  meetingId.toString()+"/"+source+"/"+file.getOriginalFilename(), 
							  convFile
							);
				} else {
					s3client.putObject(
							  ApplicationConstant.AWS_BUCKET_NAME, 
							  source+"/"+userId.toString()+"/"+file.getOriginalFilename(), 
							  convFile
							);
				}
				
				document.setDocumentTitle(file.getOriginalFilename());
				document.setMeetingEntity(meetingFromDb);
				document.setCreatedOn(LocalDateTime.now());
				document.setModifiedOn(LocalDateTime.now());
				document.setDocumentSource(source);
				document.setUserId(userId);
				
				DocumentEntity savedDoc = documentRepository.save(document);
				String DocURL = getDocumentUrl(savedDoc, meetingId, source, userId);
				
				savedDoc.setUrl(DocURL);
				savedDocuments.add(savedDoc);
				
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if(source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_DOCUMENTS)) {
			savedDocuments = documentRepository.findbyMeetingId(meetingId, 
					 ApplicationConstant.DOCUMENT_SOURCE_DOCUMENTS);
			for (DocumentEntity documentEntity : savedDocuments) {
				String DocURL = getDocumentUrl(documentEntity, meetingId, source, userId);
				documentEntity.setUrl(DocURL);
			}
		}
		
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
		if(source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_RECORDINGS))
			map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		else
			map.put(ApplicationConstant.RESPONSE_DATA, savedDocuments);
		
		if(savedDocuments.size() > 0)
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.DOCUMENT_LIST_SUCCESS);
		else
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.DOCUMENT_LIST_NOT_FOUND);
		
		return map;
	}

	@Override
	public Map<String, Object> getDocumentListAPI(Long meetingId, String source, Long userId) {
		// TODO Auto-generated method stub
		List<DocumentEntity> documents = null;
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<Object> mapResponse2 = new ArrayList<>();

		if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
			
			if(source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_RECORDINGS) && 
					meetingId == 0) {
				documents = 
						documentRepository.findbyUserId(userId,source);
			} else {
				documents = 
						documentRepository.findbyMeetingId(meetingId,source);
			}
			
		} else {
			documents = 
					documentRepository.findbyUserId(userId,source);
		}
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_EAST_1)
				  .build();
		
		if(!source.equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_PROFILE)) {
			for (DocumentEntity documentEntity : documents) {
				String docTitle = documentEntity.getDocumentTitle();
				String url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,
						documentEntity.getMeetingEntity().getMeetingId()+"/"+source+"/"+docTitle);
				documentEntity.setUrl(url);
				
				Map<String, Object> mapResponse = new HashMap<String, Object>();
				
				mapResponse.put("documentTitle",documentEntity.getDocumentTitle());
				mapResponse.put("url",url);
				mapResponse.put("documentId", documentEntity.getDocumentId());
				mapResponse.put("userId", documentEntity.getUserId());
				mapResponse.put("createdOn", documentEntity.getCreatedOn());
				mapResponse.put("meetingId",documentEntity.getMeetingEntity().getMeetingId());
				mapResponse.put("meetingTitle",documentEntity.getMeetingEntity().getMeetingTitle());
				
				mapResponse2.add(mapResponse);
				
			}
		} else {
			for (DocumentEntity documentEntity : documents) {
				String docTitle = documentEntity.getDocumentTitle();
				String url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,
						source+"/"+userId+"/"+docTitle);
				documentEntity.setUrl(url);
			}
		}
		
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
		map.put(ApplicationConstant.RESPONSE_DATA, mapResponse2);
		
		if(documents.size() > 0)
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.DOCUMENT_LIST_SUCCESS);
		else
			map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.DOCUMENT_LIST_NOT_FOUND);
		
		return map;
		
	}

	@Override
	public Map<String, Object> deleteDocumentAPI(Long documentId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		
		Optional<DocumentEntity> documents = null;
		List<DocumentEntity> allDocuments = null;
		
		documents = 
				documentRepository.findById(documentId);
		
		  if (documents.isPresent()) {
			  
			  DocumentEntity documentEntity = documents.get();
			  
			  if(documentEntity.getDocumentSource().equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_RECORDINGS) || 
					  documentEntity.getDocumentSource().equalsIgnoreCase(ApplicationConstant.DOCUMENT_SOURCE_DOCUMENTS)) {
				  AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
					
				  AmazonS3 s3client = AmazonS3ClientBuilder
						  .standard()
						  .withCredentials(new AWSStaticCredentialsProvider(credentials))
						  .withRegion(Regions.US_EAST_1)
						  .build();
					
				  s3client.deleteObject(ApplicationConstant.AWS_BUCKET_NAME,documentEntity.getMeetingEntity().getMeetingId()+"/"+documentEntity.getDocumentSource()+"/"+documentEntity.getDocumentTitle());
				  
				  documentRepository.deleteById(documentId);
				  
				  allDocuments = documentRepository.findbyMeetingId(documentEntity.getMeetingEntity().getMeetingId(), 
						 ApplicationConstant.DOCUMENT_SOURCE_DOCUMENTS);
				  
				  map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
				  map.put(ApplicationConstant.RESPONSE_DATA, allDocuments);
				  map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.DOCUMENT_DELETE_SUCCESS);
				
				  return map;
				  
			  }
		  }
		  
		  map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
		  map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		  map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.DOCUMENT_NOT_FOUND);
		  
		  return map;
		
	}
	
}
