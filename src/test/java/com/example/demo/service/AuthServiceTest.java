package com.example.demo.service;

import com.example.demo.JwtUtil;
import com.example.demo.exception.DemoException;
import com.example.demo.exception.TokenExpiredException;
import com.example.demo.exception.TokenInvalidException;
import com.example.demo.security.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class AuthServiceTest {

    private AuthService underTest = new AuthServiceImpl();

    private KeyPair keyPair = JwtUtil.getKeyPair();

    @Before
    public void setUp() {
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        ReflectionTestUtils.setField(underTest, "jwtPublicKey", publicKey);
    }

    //TODO: generate them
    private String validToken() {
        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(10, ChronoUnit.MINUTES));
        Date tmfn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        User user = new User();
        user.setUserId(1);
        user.setPublisherIds(Arrays.asList(1));
        return JwtUtil.sign(keyPair.getPrivate(), user, "api", tmfn);
    }

    private String expiredToken() {
        User user = new User();
        user.setUserId(1);
        user.setPublisherIds(Arrays.asList(1));
        return JwtUtil.sign(keyPair.getPrivate(), user, "api", new Date());
    }

    @Test
    public void givenValidJwt_validateToken_shouldOK() throws DemoException {
        User user = underTest.validateToken(validToken());
        assertThat(user, notNullValue());
    }

    @Test(expected = TokenInvalidException.class)
    public void givenInvalidJwt_validateToken_shouldThrowException() throws DemoException {
        underTest.validateToken("fake");
    }

    @Test(expected = TokenExpiredException.class)
    public void givenExpiredJwt_validateToken_shouldThrowException() throws DemoException {
        underTest.validateToken(expiredToken());
    }
}
