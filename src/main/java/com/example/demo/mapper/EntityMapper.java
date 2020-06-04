package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class EntityMapper<E, D> {
    protected abstract Class<E> getEntityClass();

    public abstract D mapToDto(E entity);

    public abstract E updateEntityFromDto(E entity, D dto) ;

    public E mapToEntity(D dto){
        E entity;
        try {
            entity = getEntityClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        return updateEntityFromDto(entity, dto);
    }

    public List<D> mapToDtoList(List<E> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
