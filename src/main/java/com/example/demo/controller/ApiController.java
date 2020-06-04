package com.example.demo.controller;

import com.example.demo.dto.ApiDTO;
import com.example.demo.mapper.ApiMapper;
import com.example.demo.service.ApiService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/apis")
public class ApiController {

    @Autowired
    private ApiMapper mapper;

    @Autowired
    private ApiService service;
    
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiOperation("Return Api information by id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Get Api successfully", response = ApiDTO.class)})
    public ResponseEntity<ApiDTO> getApi(@PathVariable(value = "id") Integer id) {
        ApiDTO dto = mapper.mapToDto(service.get(id));
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.OK);
    }
}
