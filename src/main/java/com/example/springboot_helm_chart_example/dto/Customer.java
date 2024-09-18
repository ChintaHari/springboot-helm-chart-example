package com.example.springboot_helm_chart_example.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    int id;
    String name;
    String email;
    String gender;
}
