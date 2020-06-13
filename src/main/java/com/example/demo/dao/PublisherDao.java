package com.example.demo.dao;

import com.example.demo.constant.ErrorCode;
import com.example.demo.entity.Publisher;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PublisherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static com.example.demo.constant.CacheName.API;
import static com.example.demo.constant.CacheName.PUBLISHER;

@Repository
public class PublisherDao implements GenericDao<Publisher, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDao.class);

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    @Cacheable(value = PUBLISHER, key = "#id", unless = "#result == null")
    public Publisher getById(Integer id) throws ResourceNotFoundException {
        return publisherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PUBLISHER_NOT_FOUND.toString(), "Publisher not found " + id));
    }

    @Override
    @CachePut(value = PUBLISHER, key = "#result.id")
    public Publisher upsert(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    @Override
    @CacheEvict(value = API, key = "#id")
    public void delete(Integer id) {
        publisherRepository.deleteById(id);
    }
}
