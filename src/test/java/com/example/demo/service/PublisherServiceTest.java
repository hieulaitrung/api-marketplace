package com.example.demo.service;

import com.example.demo.constant.ErrorCode;
import com.example.demo.dao.PublisherDao;
import com.example.demo.entity.Publisher;
import com.example.demo.exception.DemoException;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class PublisherServiceTest {

    @Mock
    private PublisherDao publisherDao;

    @InjectMocks
    private PublisherServiceImpl underTest;

    @Test
    public void givenPublisher_getById_shouldOK() throws DemoException {
        Integer id = 1;
        when(publisherDao.getById(id)).thenReturn(new Publisher());
        Publisher publisher = underTest.getById(id);
        assertThat(publisher, notNullValue());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void givenNoPublisher_getById_shouldThrowException() throws DemoException {
        Integer id = 1;
        when(publisherDao.getById(id)).thenThrow(new ResourceNotFoundException(ErrorCode.PUBLISHER_NOT_FOUND.toString(), "none"));
        underTest.getById(id);
    }
}
