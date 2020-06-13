package com.example.demo.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Getter
@Setter
public class User {
    private Integer userId;
    private List<Integer> publisherIds;
    private List<String> authorities;

    public static User getCurrent() {
        SecurityContext context = SecurityContextHolder.getContext();
        return (User) context.getAuthentication().getPrincipal();
    }
}
