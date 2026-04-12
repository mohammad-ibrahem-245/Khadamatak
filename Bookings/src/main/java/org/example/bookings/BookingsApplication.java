package org.example.bookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingsApplication.class, args);
	}

}
