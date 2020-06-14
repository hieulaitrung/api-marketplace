package com.example.demo.controller;

import com.example.demo.TestingWebApplication;
import com.example.demo.constant.ErrorCode;
import com.example.demo.controller.ApiController;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.TokenExpiredException;
import com.example.demo.security.JWTAuthenticationFilter;
import com.example.demo.security.User;
import com.example.demo.service.ApiService;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiController.class)
@ContextConfiguration(classes={TestingWebApplication.class})
public class ApiControllerTest {
    

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private ApiService apiService;

    @MockBean
    private AuthService authService;


    @Before
    public void setup() {
        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter(authService);
        mvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilters(authenticationFilter)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private User mockAuth(String token) throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setPublisherIds(Collections.singletonList(1));
        user.setAuthorities(Collections.singletonList("api"));
        when(authService.validateToken(token)).thenReturn(user);
        return user;
    }

    private User mockUnauth(String token) throws Exception {
        User user = new User();
        user.setUserId(2);
        user.setPublisherIds(Collections.singletonList(1));
        user.setAuthorities(Collections.singletonList("none"));
        when(authService.validateToken(token)).thenReturn(user);
        return user;
    }

    private User mockTokenExpired(String token) throws Exception {
        when(authService.validateToken(token)).thenThrow(new TokenExpiredException("Token is expired", null));
        return null;
    }

    private ApiRequestDTO mockApiRequest(){
        ApiRequestDTO dto =  new ApiRequestDTO();
        dto.setName("name");
        dto.setDescription("description");
        dto.setPublisherId(1);
        return dto;
    }

    @Test
    public void givenNoOne_whenGetAPI_thenStatus401() throws Exception {
        int id = 1;
        mvc.perform(get("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.TOKEN_INVALID.toString())));
    }

    @Test
    public void givenUnauthorizedUser_whenGetAPI_thenStatus403() throws Exception {
        int id = 1;
        String token = "fake";
        mockUnauth(token);
        mvc.perform(get("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.TOKEN_UNAUTHORIZED.toString())));
    }

    @Test
    public void givenExpiredToken_whenGetAPI_thenStatus401() throws Exception {
        int id = 1;
        String token = "fake";
        mockTokenExpired(token);
        mvc.perform(get("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.TOKEN_EXPIRED.toString())));
    }

    @Test
    public void givenAuthorizedUserAndApiNotFound_whenGetAPI_thenStatus404() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.get(anyInt(), any(User.class))).thenThrow(new ResourceNotFoundException(ErrorCode.API_NOT_FOUND.toString(), "Api not found " + id));

        mvc.perform(get("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.API_NOT_FOUND.toString())));
    }

    @Test
    public void givenAuthorizedUserAndUnauthorizedPublisher_whenGetAPI_thenStatus403() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.get(anyInt(), any(User.class))).thenThrow(new ForbiddenException(ErrorCode.PUBLISHER_UNAUTHORIZED.toString(), "User doesn’t belong to given publisher"));

        mvc.perform(get("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.PUBLISHER_UNAUTHORIZED.toString())));
    }

    @Test
    public void givenAuthorizedUserAndApiNotFound_whenGetAPIs_thenStatus200() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.queryDTO(eq(null), eq(null), anyInt(), anyInt(), any(User.class))).thenReturn(ApiQueryDTO.none());
        mvc.perform(get("/apis")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    public void givenAuthorizedUserAndApiNotFound_whenUpdateAPI_thenStatus404() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.update(anyInt(), any(ApiRequestDTO.class), any(User.class))).thenThrow(new ResourceNotFoundException(ErrorCode.API_NOT_FOUND.toString(), "Api not found " + id));

        ObjectMapper mapper = new ObjectMapper();
        String req = mapper.writeValueAsString(mockApiRequest());

        mvc.perform(put("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.API_NOT_FOUND.toString())));
    }

    @Test
    public void givenAuthorizedUserAndUnauthorizedPublisher_whenUpdateAPI_thenStatus403() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.update(anyInt(), any(ApiRequestDTO.class), any(User.class))).thenThrow(new ForbiddenException(ErrorCode.PUBLISHER_UNAUTHORIZED.toString(), "User doesn’t belong to given publisher"));

        ObjectMapper mapper = new ObjectMapper();
        String req = mapper.writeValueAsString(mockApiRequest());

        mvc.perform(put("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.PUBLISHER_UNAUTHORIZED.toString())));
    }

    @Test
    public void givenAuthorizedUserAndPublisherNotFound_whenUpdateAPI_thenStatus404() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.update(anyInt(), any(ApiRequestDTO.class), any(User.class))).thenThrow(new ResourceNotFoundException(ErrorCode.PUBLISHER_NOT_FOUND.toString(), "Publisher not found " + id));

        ObjectMapper mapper = new ObjectMapper();
        String req = mapper.writeValueAsString(mockApiRequest());

        mvc.perform(put("/apis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.PUBLISHER_NOT_FOUND.toString())));
    }
    
    @Test
    public void givenAuthorizedUserAndPublisherNotFound_whenCreateAPI_thenStatus404() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.create(any(ApiRequestDTO.class), any(User.class))).thenThrow(new ResourceNotFoundException(ErrorCode.PUBLISHER_NOT_FOUND.toString(), "Publisher not found " + id));

        ObjectMapper mapper = new ObjectMapper();
        String req = mapper.writeValueAsString(mockApiRequest());

        mvc.perform(post("/apis/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.PUBLISHER_NOT_FOUND.toString())));
    }

    @Test
    public void givenAuthorizedUserAndUnauthorizedPublisher_whenCreateAPI_thenStatus403() throws Exception {
        String token = "fake";
        mockAuth(token);
        int id = 1;
        when(apiService.create(any(ApiRequestDTO.class), any(User.class))).thenThrow(new ForbiddenException(ErrorCode.PUBLISHER_UNAUTHORIZED.toString(), "User doesn’t belong to given publisher"));

        ObjectMapper mapper = new ObjectMapper();
        String req = mapper.writeValueAsString(mockApiRequest());

        mvc.perform(post("/apis/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.PUBLISHER_UNAUTHORIZED.toString())));
    }
}
