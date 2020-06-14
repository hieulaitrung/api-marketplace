package com.example.demo.service;

import com.example.demo.dao.PublisherDao;
import com.example.demo.entity.Publisher;
import com.example.demo.exception.DemoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private PublisherDao publisherDao;

    @Override
    public Publisher getById(Integer id) throws DemoException {
        return publisherDao.getById(id);
    }
}
