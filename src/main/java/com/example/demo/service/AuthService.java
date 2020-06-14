package com.example.demo.service;

import com.example.demo.exception.DemoException;
import com.example.demo.security.User;

public interface AuthService {

    User validateToken(String token) throws DemoException;
}
