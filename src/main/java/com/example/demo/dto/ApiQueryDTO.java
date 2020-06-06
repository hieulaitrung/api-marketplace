package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiQueryDTO {

    private Long total;
    private Integer page;
    private Integer size;

    @JsonProperty("apis")
    private List<ApiDTO> apiDTOS;

}
