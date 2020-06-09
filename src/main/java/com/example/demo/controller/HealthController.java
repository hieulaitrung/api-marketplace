package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/health")
public class HealthController {
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<Object> healthCheck(){
        //SecurityContext context = SecurityContextHolder.getContext();
        //User userDetails =  (User) context.getAuthentication().getPrincipal();
        return new ResponseEntity<>("{\"status\" :\"OK\"}", new HttpHeaders(), HttpStatus.OK);
    }
}
