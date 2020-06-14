package com.example.demo.integration;

import com.example.demo.JwtUtil;
import com.example.demo.constant.BusinessType;
import com.example.demo.elasticsearch.ApiDocument;
import com.example.demo.elasticsearch.ApiPublisher;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.entity.Publisher;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.PublisherRepository;
import com.example.demo.security.JWTAuthenticationFilter;
import com.example.demo.security.User;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiIntegrationTest extends  BaseIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ElasticsearchOperations operations;

    private Publisher publisher;

    private Api api;

    private KeyPair keyPair = JwtUtil.getKeyPair();

    private String token;

    final static ObjectMapper MAPPER = new ObjectMapper();

    private static String asJsonString(Object obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        prepareIndex();
        prepareJwt();

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

        ApiDocument apiDocument = new ApiDocument();
        apiDocument.setId(api.getId());
        apiDocument.setName(api.getName());
        apiDocument.setDescription(api.getDescription());
        apiDocument.setDoc(api.getDoc());
        apiDocument.setPublisher(ApiPublisher.builder()
                .businessType(api.getPublisher().getBusinessType().toString())
                .build());
        operations.save(apiDocument);
        operations.indexOps(ApiDocument.class).refresh();

        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(10, ChronoUnit.MINUTES));
        Date tmfn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        User user = new User();
        user.setUserId(1);
        user.setPublisherIds(Arrays.asList(publisher.getId()));
        token = JwtUtil.sign(keyPair.getPrivate(), user, "api", tmfn);
    }

    private void prepareJwt(){
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        ReflectionTestUtils.setField(authService, "jwtPublicKey", publicKey);
    }

    private void prepareIndex() throws IOException {
        String esMapping = "classpath:es/api_mapping.json";
        File file = ResourceUtils.getFile(esMapping);
        String mapping = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        operations.indexOps(ApiDocument.class).create();
        operations.indexOps(ApiDocument.class).putMapping(Document.parse(mapping));
    }

    @After
    public void tearDown(){
        apiRepository.deleteAll();
        publisherRepository.deleteAll();
        operations.indexOps(ApiDocument.class).delete();
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
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token)
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
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(api.getId())))
                .andExpect(jsonPath("$.name", is(api.getName())))
                .andExpect(jsonPath("$.publisherId", is(publisher.getId())));
    }

    @Test
    public void givenNothing_whenGetAPIs_thenStatus200() throws Exception {
        mvc.perform(get("/apis", api.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.apis").isArray())
                .andExpect(jsonPath("$.apis", hasSize(1)))
                .andExpect(jsonPath("$.apis[0].id", is(api.getId())));
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
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token)
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
                .header(JWTAuthenticationFilter.AUTHORIZATION, JWTAuthenticationFilter.PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
