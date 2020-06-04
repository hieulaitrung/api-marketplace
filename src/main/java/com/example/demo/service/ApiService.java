package com.example.demo.service;

import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;

public interface ApiService {

    Api get(Integer id);

    Api create(ApiRequestDTO dto);

    Api update(Integer id, ApiRequestDTO dto);

    void delete(Integer id);
    
}
