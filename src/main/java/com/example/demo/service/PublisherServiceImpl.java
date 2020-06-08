package com.example.demo.service;

import com.example.demo.dao.PublisherDao;
import com.example.demo.entity.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private PublisherDao publisherDao;

    @Override
    public Publisher getById(Integer id) {
        return publisherDao.getById(id);
    }
}
