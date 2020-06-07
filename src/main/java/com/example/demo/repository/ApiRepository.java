package com.example.demo.repository;

import com.example.demo.entity.Api;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRepository extends JpaRepository<Api, Integer> {
}
