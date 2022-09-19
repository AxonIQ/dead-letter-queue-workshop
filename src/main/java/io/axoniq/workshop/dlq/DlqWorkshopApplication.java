package io.axoniq.workshop.dlq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class DlqWorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(DlqWorkshopApplication.class, args);
	}

}
