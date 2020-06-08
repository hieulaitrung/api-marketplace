package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiQueryDTO {

    private Long total;

    @JsonProperty("apis")
    private List<ApiDTO> apiDTOS;

}
