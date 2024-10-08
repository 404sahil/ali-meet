package com.api.alimeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"com.api"})
@EntityScan(basePackages = {"com.api"})
@EnableAsync
@EnableJpaAuditing
public class AlimeetApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AlimeetApplication.class, args);
	}

}
