package com.example.demo.service;

import com.example.demo.constant.BusinessType;
import com.example.demo.constant.ErrorCode;
import com.example.demo.dao.ApiDao;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.entity.Publisher;
import com.example.demo.exception.DemoException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ApiMapper;
import com.example.demo.security.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class ApiServiceTest {

    @Mock
    private PublisherService publisherService;

    @Mock
    private ApiDao apiDao;

    @InjectMocks
    private ApiServiceImpl underTest;

    private Publisher publisher(){
        Publisher publisher = new Publisher();
        publisher.setId(1);
        publisher.setName("Publisher");
        publisher.setBusinessType(BusinessType.PRIVATE);
        return publisher;
    }

    private Api api(){
        Api api = new Api();
        api.setId(1);
        api.setName("Api");
        api.setDescription("description");
        api.setDoc("doc");
        api.setPublisher(publisher());
        return api;
    }

    private ApiRequestDTO apiRequest(){
        ApiRequestDTO dto = new ApiRequestDTO();
        dto.setName("New api");
        dto.setDescription("Description");
        dto.setDoc("Doc");
        dto.setPublisherId(1);
        return  dto;
    }

    private User user(){
        User user = new User();
        user.setUserId(1);
        user.setPublisherIds(Collections.singletonList(1));
        return user;
    }

    private User unauthorizedUser(){
        User user = new User();
        user.setUserId(2);
        user.setPublisherIds(Collections.singletonList(2));
        return user;
    }

    @Test
    public void givenAuthorizedUser_getById_shouldOK() throws DemoException {
        Integer id = 1;
        when(apiDao.getById(id)).thenReturn(api());
        Api api = underTest.get(id, user());
        assertThat(api, notNullValue());
        assertThat(api.getId(), is(id));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void givenAuthorizedUserNotFoundApi_getById_shouldThrowException() throws DemoException {
        Integer id = 1;
        when(apiDao.getById(id)).thenThrow(new ResourceNotFoundException(ErrorCode.API_NOT_FOUND.toString(), "none"));
        underTest.get(id, user());
    }

    @Test(expected = ForbiddenException.class)
    public void givenUnauthorizedUser_getById_shouldThrowException() throws DemoException {
        Integer id = 1;
        when(apiDao.getById(id)).thenReturn(api());
        underTest.get(id, unauthorizedUser());
    }

    @Test
    public void givenAuthorizedUser_create_shouldOK() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        api = underTest.create(apiRequest(), user());
        assertThat(api, notNullValue());
    }

    @Test(expected = ForbiddenException.class)
    public void givenUnauthorizedUser_create_shouldThrowException() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        underTest.create(apiRequest(), unauthorizedUser());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void givenUnauthorizedUserAndNotFoundPublisher_create_shouldThrowException() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(publisherService.getById(anyInt())).thenThrow(new ResourceNotFoundException(ErrorCode.PUBLISHER_NOT_FOUND.toString(), "none"));
        underTest.create(apiRequest(), user());
    }

    @Test
    public void givenAuthorizedUser_update_shouldOK() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(apiDao.getById(anyInt())).thenReturn(api());
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        api = underTest.update(api.getId(), apiRequest(), user());
        assertThat(api, notNullValue());
    }

    @Test(expected = ForbiddenException.class)
    public void givenUnauthorizedUser_update_shouldThrowException() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(apiDao.getById(anyInt())).thenReturn(api());
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        underTest.update(api.getId(), apiRequest(), unauthorizedUser());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void givenUnauthorizedUserAndNotFoundPublisher_update_shouldThrowException() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(apiDao.getById(anyInt())).thenReturn(api());
        when(publisherService.getById(anyInt())).thenThrow(new ResourceNotFoundException(ErrorCode.PUBLISHER_NOT_FOUND.toString(), "none"));
        underTest.update(api.getId(), apiRequest(), unauthorizedUser());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void givenAuthorizedUserNotFoundApi_update_shouldThrowException() throws DemoException {
        Api api = api();
        when(apiDao.upsert(any(Api.class))).thenReturn(api);
        when(apiDao.getById(anyInt())).thenThrow(new ResourceNotFoundException(ErrorCode.API_NOT_FOUND.toString(), "none"));
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        underTest.update(api.getId(), apiRequest(), user());
    }

    @Test
    public void givenAuthorizedUser_delete_shouldOK() throws DemoException {
        Api api = api();
        doNothing().when(apiDao).delete(anyInt());
        when(apiDao.getById(anyInt())).thenReturn(api());
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        underTest.delete(api.getId(), user());
    }

    @Test(expected = ForbiddenException.class)
    public void givenUnauthorizedUser_delete_shouldThrowException() throws DemoException {
        Api api = api();
        doNothing().when(apiDao).delete(anyInt());
        when(apiDao.getById(anyInt())).thenReturn(api());
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        underTest.delete(api.getId(), unauthorizedUser());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void givenAuthorizedUserNotFoundApi_delete_shouldThrowException() throws DemoException {
        Api api = api();
        doNothing().when(apiDao).delete(anyInt());
        when(apiDao.getById(anyInt())).thenThrow(new ResourceNotFoundException(ErrorCode.API_NOT_FOUND.toString(), "none"));
        when(publisherService.getById(anyInt())).thenReturn(publisher());
        underTest.delete(api.getId(), user());
    }

    @Test
    public void givenAuthorizedUser_query_shouldOK() {
        User user =  user();
        Api api = api();
        ApiQueryDTO dto = new ApiQueryDTO();
        dto.setTotal(1L);
        dto.setApiDTOS(Collections.singletonList(ApiMapper.mapToDto(api)));
        when(apiDao.queryByPublisherIds(user.getPublisherIds(), null, 0, 10)).thenReturn(dto);

        ApiQueryDTO queryDTO = underTest.queryDTO(null, null, 0, 10, user);
        assertThat(queryDTO.getTotal(), is(1L));
        assertThat(queryDTO.getApiDTOS().get(0).getId(), is(api.getId()));
    }
}
