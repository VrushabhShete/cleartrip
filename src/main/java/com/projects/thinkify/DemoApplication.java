package com.projects.thinkify;

import com.projects.thinkify.controller.DriverController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

		DriverController driver = context.getBean(DriverController.class);
		driver.init();
	}

}
