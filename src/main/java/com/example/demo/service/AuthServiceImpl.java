package com.example.demo.service;

import com.example.demo.constant.BusinessType;
import com.example.demo.constant.ErrorCode;
import com.example.demo.entity.Publisher;
import com.example.demo.exception.*;
import com.example.demo.security.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements  AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${jwt.publicKey}")
    private String jwtPublicKey;

    @Override
    public void validateAccess(User user, Publisher publisher) throws ForbiddenException {
        if (BusinessType.PRIVATE.equals(publisher.getBusinessType()) && !user.getPublisherIds().contains(publisher.getId())) {
            LOGGER.warn("User {} doesn’t belong to given publisher {}", user.getUserId(), publisher.getId());
            throw new ForbiddenException(ErrorCode.PUBLISHER_UNAUTHORIZED.toString(), "User doesn’t belong to given publisher");
        }
    }

    public User validateToken(String token) throws BaseException {
        User user = new User();

        Claims claims = getClaims(token);

        user.setUserId(Integer.valueOf(claims.getSubject()));
        String publisherIds = claims.get("publishers", String.class);
        Optional.ofNullable(publisherIds).ifPresent(p -> user.setPublisherIds(Arrays.stream(p.split(" ")).map(Integer::valueOf).collect(Collectors.toList())));
        String scope = claims.get("scope", String.class);
        Optional.ofNullable(scope)
                .map(s -> Arrays.asList(s.split(" ")))
                .ifPresent(user::setAuthorities);
        return user;
    }

    private Claims getClaims(String token) throws BaseException {
        try {
            PublicKey key = decodePublicKey(pemToDer(jwtPublicKey));
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (DecodingException | MalformedJwtException e) {
            LOGGER.warn("Token is invalid {}", token);
            throw new TokenInvalidException("Invalid token", e);
        } catch (ExpiredJwtException e) {
            LOGGER.warn("Token is expired {}", token);
            throw new TokenExpiredException("Token is expired", e);
        } catch (Exception e) {
            LOGGER.error("Error to getClaims from token {}", token);
            throw new SystemErrorException("Error to getClaims from token", e);
        }
    }

    private byte[] pemToDer(String pem) {
        return Base64.getDecoder().decode(pem);
    }

    private PublicKey decodePublicKey(byte[] der) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
