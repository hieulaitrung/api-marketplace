package com.example.demo;

import com.example.demo.security.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUtil {
    public static KeyPair getKeyPair(){
         return Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    public static String sign(Key privateKey, User user, String scope, Date  expiredDate){
        Map<String, Object> claims =  new HashMap<>();
        claims.put("scope", scope);
        claims.put("publishers", String.join(" ", user.getPublisherIds().stream().map(String::valueOf).collect(Collectors.toList())));

        final String token = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setExpiration(expiredDate)
                .addClaims(claims)
                .signWith(privateKey).compact();
        return token;
    }
}
