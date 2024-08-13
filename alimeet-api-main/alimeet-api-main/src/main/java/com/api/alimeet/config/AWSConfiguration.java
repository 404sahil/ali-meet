package com.api.alimeet.config;

import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class AWSConfiguration {

	@Value("${alimeet.aws.accesskey}")
	public static String accesskey;

	@Value("${alimeet.aws.secretkey}")
	public static String secretkey;
	
	public static AWSCredentials getAwsCreds() {
		AWSCredentials credentials = new BasicAWSCredentials(accesskey, secretkey);
		return credentials;
	}
}
