package com.api.alimeet.controller;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
import com.api.alimeet.config.JwtTokenUtil;
import com.api.alimeet.constant.ApplicationConstant;
import com.api.alimeet.model.DocumentEntity;
import com.api.alimeet.model.JwtRequest;
import com.api.alimeet.model.JwtResponse;
import com.api.alimeet.model.UserDto;
import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.DocumentRepository;
import com.api.alimeet.repository.UserRepository;
import com.api.alimeet.service.RegistrationService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Value("${alimeet.aws.accesskey}")
	private String awsAccessKey;

	@Value("${alimeet.aws.secretkey}")
	private String awsSecretKey;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired @Qualifier("theJwtTokenUtil")
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<Map> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)
			throws Exception {
		
		 Map<String, Object> map = new HashMap<String, Object>();
		 Map<String, Object> mapResponse = new HashMap<String, Object>();
		 Map<String, Object> mapResponseUserData = new HashMap<String, Object>();
		 List<DocumentEntity> documents = null;
		 String url = null;

		 try {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

			final UserDetails userDetails = jwtInMemoryUserDetailsService
					.loadUserByUsername(authenticationRequest.getUsername());

			final String token = jwtTokenUtil.generateToken(userDetails);
			mapResponse.put("token", token);
			
			Optional<UserEntity> userEntity = userRepository.findOneByEmailIgnoreCase(userDetails.getUsername());

			if(userEntity.isPresent()) {
				UserEntity userData = userEntity.get();
				AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
				
				AmazonS3 s3client = AmazonS3ClientBuilder
						  .standard()
						  .withCredentials(new AWSStaticCredentialsProvider(credentials))
						  //.withResponseHeaders(new ResponseHeaderOverrides().withContentDisposition("attachment; filename*=UTF-8''\"A,B\""))
						  .withRegion(Regions.US_EAST_1)
						  .build();
				
				documents = 
						documentRepository.findbyUserId(userData.getId(),
								ApplicationConstant.DOCUMENT_SOURCE_PROFILE);
				
				for (DocumentEntity documentEntity : documents) {
					String docTitle = documentEntity.getDocumentTitle();
					url = getSignedURL(s3client, ApplicationConstant.AWS_BUCKET_NAME,
							ApplicationConstant.DOCUMENT_SOURCE_PROFILE+"/"+userData.getId()+"/"+docTitle);
				}
				String initialName = printInitials(userData.getUserName());
				
				mapResponseUserData.put("id", userData.getId().toString());
				mapResponseUserData.put("userName", userData.getUserName());
				mapResponseUserData.put("email", userData.getEmail());
				mapResponseUserData.put("role", userData.getRole());
				mapResponseUserData.put("url", url);
				mapResponseUserData.put("token", token);
				mapResponseUserData.put("initialName", initialName.trim());
				
				mapResponse.put("userData", mapResponseUserData);
				
				if(userData.getIsVerified() != null && userData.getIsVerified().equals(true)) {
					map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_200);
					map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.LOGIN_SUCCESS);
					map.put(ApplicationConstant.RESPONSE_DATA, mapResponseUserData);
					return ResponseEntity.ok(map);
				}
			}
			
		} catch(Exception e) {
			if(e.getMessage().equalsIgnoreCase("INVALID_CREDENTIALS")) {
				map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
				map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.LOGIN_INVALID_CREDENTIALS);
				map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
				return ResponseEntity.ok(map);
			}
		}
		map.put(ApplicationConstant.RESPONSE_STATUS, ApplicationConstant.STATUS_400);
		map.put(ApplicationConstant.RESPONSE_MESSAGE, ApplicationConstant.LOGIN_NOT_VERIFIED);
		map.put(ApplicationConstant.RESPONSE_DATA, new ArrayList<>());
		return ResponseEntity.ok(map);
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception(ApplicationConstant.LOGIN_INVALID_CREDENTIALS, e);
		}
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
	
	String printInitials(String name)
    {
		String initialLetter = "";
			
        if (name.length() == 0)
            return initialLetter;
        
        //split the string using 'space'
        //and print the first character of every word
        String words[] = name.split(" ");
        for(String word : words) {
            initialLetter += Character.toUpperCase(word.charAt(0));
        }
        
        return initialLetter;
    }
}
