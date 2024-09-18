package com.example.springboot_helm_chart_example;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot_helm_chart_example.dto.Customer;

@SpringBootApplication
@RestController
@RequestMapping("/customers")
public class SpringbootHelmChartExampleApplication {

	@GetMapping
	public List<Customer> getCustomers() {
		return Stream.of(
			new Customer(1, "Hari Krishna", "harikrishna@gmail.com", "male"),
			new Customer(2, "Rakesh Kumar", "rakesh@gmail.com", "male"),
			new Customer(3, "Tarun Kumar", "tarun@gmail.com", "male"))
			.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootHelmChartExampleApplication.class, args);
	}

}
