package com.example.demo.service;

import com.example.demo.entity.Publisher;
import com.example.demo.exception.DemoException;

public interface PublisherService {
    Publisher getById(Integer id) throws DemoException;
}
