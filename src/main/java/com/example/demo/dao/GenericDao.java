package com.example.demo.dao;

import java.io.Serializable;

public interface GenericDao<D, K extends Serializable> {
    D getById(K id);

    D upsert(D d);

    void delete(K id);
}
