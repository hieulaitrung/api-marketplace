package com.example.demo.service;

import com.example.demo.entity.Publisher;
import com.example.demo.exception.BaseException;

public interface PublisherService {
    Publisher getById(Integer id) throws BaseException;
}
