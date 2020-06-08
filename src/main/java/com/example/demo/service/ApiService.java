package com.example.demo.service;

import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;



public interface ApiService {

    ApiQueryDTO queryDTO(String id, String term, Integer page, Integer size);

    Api get(Integer id);

    Api create(ApiRequestDTO dto);

    Api update(Integer id, ApiRequestDTO dto);

    void delete(Integer id);
    
}
