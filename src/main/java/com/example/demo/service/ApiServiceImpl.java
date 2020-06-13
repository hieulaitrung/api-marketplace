package com.example.demo.service;

import com.example.demo.dao.ApiDao;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.entity.Publisher;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ApiServiceImpl implements ApiService {
    @Autowired
    private PublisherService publisherService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApiDao apiDao;

    @Override
    public ApiQueryDTO queryDTO(String term, Integer publisherId, Integer page, Integer size) {
        List<Integer> queryPublisherIds = User.getCurrent().getPublisherIds();
        if (publisherId != null) {
            queryPublisherIds = queryPublisherIds.contains(publisherId) ? Collections.singletonList(publisherId) : null;
        }
        return apiDao.queryByPublisherIds(queryPublisherIds, term, page, size);
    }

    @Override
    public Api get(Integer id) throws ForbiddenException {
        Api api = apiDao.getById(id);
        authService.validateAccess(User.getCurrent(), api.getPublisher());
        return api;
    }

    @Override
    public Api create(ApiRequestDTO dto) throws ForbiddenException {
        Publisher publisher = publisherService.getById(dto.getPublisherId());
        authService.validateAccess(User.getCurrent(), publisher);

        Api api = new Api();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setCreatedOn(new Date());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisher);

        return apiDao.upsert(api);
    }

    @Override
    public Api update(Integer id, ApiRequestDTO dto) throws ForbiddenException {
        Api api = get(id);
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherService.getById(dto.getPublisherId()));

        return apiDao.upsert(api);
    }

    @Override
    public void delete(Integer id) throws ForbiddenException {
        get(id);
        apiDao.delete(id);
    }
}
