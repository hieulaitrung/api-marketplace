package com.example.demo.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResult<D> {

    private long total;

    private List<D> list;
}
