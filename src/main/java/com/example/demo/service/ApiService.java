package com.example.demo.service;

import com.example.demo.document.ApiDocument;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface ApiService {

    SearchHits<ApiDocument> getAll(Integer id, Integer page, Integer size);

    Api get(Integer id);

    Api create(ApiRequestDTO dto);

    Api update(Integer id, ApiRequestDTO dto);

    void delete(Integer id);
    
}
