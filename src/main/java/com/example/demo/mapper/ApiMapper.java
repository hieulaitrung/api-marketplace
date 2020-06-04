package com.example.demo.mapper;

import com.example.demo.dto.ApiDTO;
import com.example.demo.entity.Api;
import org.springframework.stereotype.Component;

@Component("DtoApiMapper")
public class ApiMapper extends EntityMapper<Api, ApiDTO> {
    @Override
    protected Class<Api> getEntityClass() {
        return Api.class;
    }

    @Override
    public ApiDTO mapToDto(Api entity) {
        ApiDTO dto = new ApiDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDoc(entity.getDoc());
        dto.setPublisherId(entity.getPublisher().getId());
        return dto;
    }

    @Override
    public Api updateEntityFromDto(Api entity, ApiDTO dto) {
        return null;
    }
}
