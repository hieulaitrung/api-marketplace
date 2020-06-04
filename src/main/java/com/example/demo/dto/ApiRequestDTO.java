package com.example.demo.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiRequestDTO {

    private String name;

    private String description;

    private String doc;

    private Integer publisherId;
}
