package com.example.demo.security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {
    private String userId;
    private List<String> publisherIds;
    private List<String> permissions;
}
