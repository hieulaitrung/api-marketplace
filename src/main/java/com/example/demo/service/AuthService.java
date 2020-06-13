package com.example.demo.service;

import com.example.demo.entity.Publisher;
import com.example.demo.exception.BaseException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.security.User;

public interface AuthService {

    void validateAccess(User user, Publisher publisher) throws ForbiddenException;

    User validateToken(String token) throws BaseException;
}
