package com.example.demo.controller;

import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/apis")
public class ApiController {
    
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ApiOperation("Return account information by id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK ", response = String.class)})
    public ResponseEntity<Object> healthCheck(){
        return new ResponseEntity<>("API", new HttpHeaders(), HttpStatus.OK);
    }
}
