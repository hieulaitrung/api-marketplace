package com.example.demo.service;

import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.exception.BaseException;
import com.example.demo.exception.ForbiddenException;


public interface ApiService {

    ApiQueryDTO queryDTO(String term, Integer publisherId, Integer page, Integer size);

    Api get(Integer id) throws BaseException;

    Api create(ApiRequestDTO dto) throws BaseException;

    Api update(Integer id, ApiRequestDTO dto) throws BaseException;

    void delete(Integer id) throws BaseException;
    
}
