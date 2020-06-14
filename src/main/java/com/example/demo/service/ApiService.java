package com.example.demo.service;

import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.exception.DemoException;
import com.example.demo.security.User;


public interface ApiService {

    ApiQueryDTO queryDTO(String term, Integer publisherId, Integer page, Integer size, User user);

    Api get(Integer id, User user) throws DemoException;

    Api create(ApiRequestDTO dto, User user) throws DemoException;

    Api update(Integer id, ApiRequestDTO dto, User user) throws DemoException;

    void delete(Integer id, User user) throws DemoException;
    
}
