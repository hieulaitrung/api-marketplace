package com.example.demo.service;

import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.exception.ForbiddenException;


public interface ApiService {

    ApiQueryDTO queryDTO(String term, Integer publisherId, Integer page, Integer size);

    Api get(Integer id) throws ForbiddenException;

    Api create(ApiRequestDTO dto) throws ForbiddenException;

    Api update(Integer id, ApiRequestDTO dto) throws ForbiddenException;

    void delete(Integer id) throws ForbiddenException;
    
}
