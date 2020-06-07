package com.example.demo.service;

import com.example.demo.elasticsearch.ApiDocument;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface ApiService {

    SearchHits<ApiDocument> getAll(String id, String term, Integer page, Integer size);

    Api get(Integer id);

    Api create(ApiRequestDTO dto);

    Api update(Integer id, ApiRequestDTO dto);

    void delete(Integer id);
    
}
