package com.example.demo.service;

import com.example.demo.entity.Api;
import com.example.demo.repository.ApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl implements  ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Override
    public Api get(Integer id) {
        return apiRepository.getOne(id);
    }
}
