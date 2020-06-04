package com.example.demo.service;

import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApiServiceImpl implements  ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    public Api get(Integer id) {
        return apiRepository.getOne(id);
    }

    @Override
    public Api create(ApiRequestDTO dto) {
        Api api = new Api();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setCreatedOn(new Date());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherRepository.getOne(dto.getPublisherId()));
        return apiRepository.save(api);
    }

    @Override
    public Api update(Integer id, ApiRequestDTO dto) {
        Api api = get(id);
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherRepository.getOne(dto.getPublisherId()));
        return apiRepository.save(api);
    }

    @Override
    public void delete(Integer id) {
        apiRepository.deleteById(id);
    }
}
