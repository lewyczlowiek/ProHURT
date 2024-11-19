package com.ProHURT;

import com.ProHURT.services.RoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProHurtApplication {
	@Autowired
	private RoleService roleService;

	public static void main(String[] args) {
		SpringApplication.run(ProHurtApplication.class, args);
	}
}
