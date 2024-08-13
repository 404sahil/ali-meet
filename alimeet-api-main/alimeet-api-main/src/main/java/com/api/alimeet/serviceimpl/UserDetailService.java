package com.api.alimeet.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.alimeet.model.UserEntity;
import com.api.alimeet.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service("customUserService")
@Slf4j
public class UserDetailService implements UserDetailsService {

	public static final Logger logger = LoggerFactory.getLogger(UserDetailService.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		logger.info("inside loadUserByUsername method");
		//Optional<UserEntity> entity = userRepository.findByEmailIgnoreCase(username);
		
		Optional<UserEntity> entity = userRepository.findOneByEmailIgnoreCase(username);
		UserEntity userEntity = null;

		
		if(entity.isPresent()) {
			userEntity = entity.get();
		}
		

		System.out.println("userEntity ===> "+userEntity);
		if (userEntity == null) {
			throw new UsernameNotFoundException("", new Throwable("Invalid Creds"));
		}
		UserDetails user = User.withUsername(userEntity.getEmail()).password(userEntity.getPassword())
				.authorities("TEACHER").build();
		return user;
		
//	String[] usernameAndDomain = StringUtils.split(
//	          username, String.valueOf(Character.LINE_SEPARATOR));
//	
		//System.out.println("username ====> "+username);
//		System.out.println("usernameAndDomain[1] ====> "+usernameAndDomain[1]);
////		User user = userRepository.findUser(usernameAndDomain[0], usernameAndDomain[1]);
//	
//		if (usernameAndDomain == null || usernameAndDomain.length != 2) {
//			throw new UsernameNotFoundException("Username and domain must be provided");
//		}
//
//		//UserEntity userEntity = userRepository.findUser(usernameAndDomain[0], usernameAndDomain[1]);
		//final UserEntity userEntity = userRepository.findUser(username,"TEACHER");
		
//		if (userEntity == null) {
//			throw new UsernameNotFoundException(username);
//		}
//		UserDetails user = User.withUsername(userEntity.getEmail()).password(userEntity.getPassword())
//				.authorities("USER").build();
//		
//		System.out.println("user =========> "+user);
//		return user;
	}

}