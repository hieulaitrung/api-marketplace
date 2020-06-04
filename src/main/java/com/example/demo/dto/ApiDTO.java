package com.example.demo.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDTO {
    private Integer id;

    private String name;

    private String description;

    private String doc;

    private Integer publisherId;
}
