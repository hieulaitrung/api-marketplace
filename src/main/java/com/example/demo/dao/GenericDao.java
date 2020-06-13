package com.example.demo.dao;

import com.example.demo.exception.ResourceNotFoundException;

import java.io.Serializable;

public interface GenericDao<D, K extends Serializable> {
    D getById(K id) throws ResourceNotFoundException;

    D upsert(D d);

    void delete(K id);
}
