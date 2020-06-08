package com.example.demo.dao;

import com.example.demo.entity.Publisher;
import com.example.demo.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import static com.example.demo.constant.CacheName.API;
import static com.example.demo.constant.CacheName.PUBLISHER;

@Repository
public class PublisherDao implements GenericDao<Publisher, Integer> {
    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    @Cacheable(value = PUBLISHER, key = "#id", unless = "#result == null")
    public Publisher getById(Integer id) {
        return publisherRepository.getOne(id);
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
