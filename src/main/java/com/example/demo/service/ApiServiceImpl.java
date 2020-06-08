package com.example.demo.service;

import com.example.demo.dao.ApiDao;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {
    @Autowired
    private PublisherService publisherService;

    @Autowired
    private ApiDao apiDao;

    @Override
    public ApiQueryDTO queryDTO(String id, String term, Integer page, Integer size) {
        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            ids = Arrays.asList(id.split(","));
        }
        return apiDao.queryByPublisherIds(ids, term, page, size);
    }

    @Override
    public Api get(Integer id) {
        return apiDao.getById(id);
    }

    @Override
    public Api create(ApiRequestDTO dto) {
        Api api = new Api();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setCreatedOn(new Date());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherService.getById(dto.getPublisherId()));

        return apiDao.upsert(api);
    }

    @Override
    public Api update(Integer id, ApiRequestDTO dto) {
        Api api = get(id);
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherService.getById(dto.getPublisherId()));

        return apiDao.upsert(api);
    }

    @Override
    public void delete(Integer id) {
        apiDao.delete(id);
    }
}
