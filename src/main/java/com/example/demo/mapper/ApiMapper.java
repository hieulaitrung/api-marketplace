package com.example.demo.mapper;

import com.example.demo.elasticsearch.ApiDocument;
import com.example.demo.dto.ApiDTO;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.entity.Api;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

public class ApiMapper {
    public static ApiDTO mapToDto(Api entity) {
        ApiDTO dto = new ApiDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDoc(entity.getDoc());
        dto.setPublisherId(entity.getPublisher().getId());
        return dto;
    }

    public static ApiQueryDTO mapToDto(SearchHits<ApiDocument> searchHits) {
        ApiQueryDTO dto = new ApiQueryDTO();
        dto.setTotal(searchHits.getTotalHits());
        dto.setApiDTOS(searchHits.get()
                .map(SearchHit::getContent)
                .map(ApiMapper::mapToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private static ApiDTO mapToDto(ApiDocument apiDocument) {
        ApiDTO dto = new ApiDTO();
        dto.setId(apiDocument.getId());
        dto.setName(apiDocument.getName());
        dto.setDescription(apiDocument.getDescription());
        dto.setDoc(apiDocument.getDoc());
        dto.setPublisherId(apiDocument.getPublisherId());
        return dto;
    }
}
