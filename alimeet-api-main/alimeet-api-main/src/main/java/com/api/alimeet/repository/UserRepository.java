package com.api.alimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.alimeet.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
	
	//@Query("SELECT p FROM UserEntity p WHERE p.email = :email")
	Optional<UserEntity> findOneByEmailIgnoreCase(String email);
	
	List<UserEntity> findByRole(@Param("role") String role);
	
	@Query("SELECT p.email, p.password, p.userName, p.role, p.meetingEntity FROM UserEntity p WHERE p.email = :email")
	List<UserEntity> findByEmail(@Param("email") String email);
	
	@Query("UPDATE UserEntity p SET p.userName= :userName, p.role= :role WHERE p.id = :id")
	List<UserEntity> updateUserData(@Param("userName") String userName, @Param("role") String role, 
			@Param("id") Long id);
	
}