package com.codewithkirk.OrderItemsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderItemsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderItemsServiceApplication.class, args);
	}

}
