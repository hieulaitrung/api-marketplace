package com.example.demo;

import com.example.demo.constant.BusinessType;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.entity.Publisher;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.PublisherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "testing=true")
public class ApiControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PublisherRepository publisherRepository;

    private Publisher publisher;

    private Api api;

    @Autowired
    private ApiRepository apiRepository;

    final static ObjectMapper MAPPER = new ObjectMapper();

    private static String asJsonString(Object obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }

    @Before
    public void setUp() {
        publisher = new Publisher();
        publisher.setName("Publisher");
        publisher.setBusinessType(BusinessType.PUBLIC);
        publisher.setCreatedOn(new Date());
        publisher.setUpdatedOn(new Date());
        publisher = publisherRepository.save(publisher);

        api = new Api();
        api.setName("Api");
        api.setDescription("Description");
        api.setDoc("Doc");
        api.setCreatedOn(new Date());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisher);
        api = apiRepository.save(api);
    }

    @Test
    public void givenNothing_whenCreateAPI_thenStatus201() throws Exception {
        ApiRequestDTO apiCreateDTO = new ApiRequestDTO();
        apiCreateDTO.setName("Api sample");
        apiCreateDTO.setDescription("Desc");
        apiCreateDTO.setDoc("yaml");
        apiCreateDTO.setPublisherId(publisher.getId());

        mvc.perform(post("/apis")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(apiCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", isA(Integer.class)))
                .andExpect(jsonPath("$.name", is("Api sample")))
                .andExpect(jsonPath("$.publisherId", is(publisher.getId())));
    }

    @Test
    public void givenNothing_whenGetAPI_thenStatus200() throws Exception {
        mvc.perform(get("/apis/{id}", api.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(api.getId())))
                .andExpect(jsonPath("$.name", is(api.getName())))
                .andExpect(jsonPath("$.publisherId", is(publisher.getId())));
    }

    @Test
    public void givenNothing_whenUpdateAPI_thenStatus201() throws Exception {
        ApiRequestDTO apiRequestDTO = new ApiRequestDTO();
        apiRequestDTO.setName("New API 1");
        apiRequestDTO.setDescription("Desc");
        apiRequestDTO.setDoc("yaml");
        apiRequestDTO.setPublisherId(publisher.getId());

        mvc.perform(put("/apis/{id}", api.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(apiRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(api.getId())))
                .andExpect(jsonPath("$.name", is("New API 1")))
                .andExpect(jsonPath("$.publisherId", is(publisher.getId())));
    }

    @Test
    public void givenNothing_whenDeleteAPI_thenStatus201() throws Exception {
        mvc.perform(delete("/apis/{id}", api.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
