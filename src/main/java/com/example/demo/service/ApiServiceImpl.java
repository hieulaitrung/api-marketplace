package com.example.demo.service;

import com.example.demo.document.ApiDocument;
import com.example.demo.document.ApiPublisher;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApiServiceImpl implements  ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public SearchHits<ApiDocument> getAll(Integer id, Integer page, Integer size) {
        String source = "{\n" +
                "    \"bool\": {\n" +
                "        \"must\": {\n" +
                "            \"term\": {\n" +
                "                \"id\": " + id + "\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        StringQuery query = new StringQuery(source, PageRequest.of(page, size));
        return operations.search(query, ApiDocument.class);
    }

    @Override
    public Api get(Integer id) {
        return apiRepository.getOne(id);
    }

    @Override
    public Api create(ApiRequestDTO dto) {
        Api api = new Api();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setCreatedOn(new Date());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherRepository.getOne(dto.getPublisherId()));
        api = apiRepository.save(api);

        ApiDocument apiDocument = new ApiDocument();
        apiDocument.setId(api.getId());
        apiDocument.setName(api.getName());
        apiDocument.setDescription(api.getDescription());
        apiDocument.setDoc(api.getDoc());
        apiDocument.setPublisherId(api.getPublisher().getId());
        apiDocument.setPublisher(ApiPublisher.builder()
                .businessType(api.getPublisher().getBusinessType().toString())
                .build());
        operations.save(apiDocument);
        return api;
    }

    @Override
    public Api update(Integer id, ApiRequestDTO dto) {
        Api api = get(id);
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherRepository.getOne(dto.getPublisherId()));
        api = apiRepository.save(api);

        ApiDocument apiDocument = new ApiDocument();
        apiDocument.setId(api.getId());
        apiDocument.setName(api.getName());
        apiDocument.setDescription(api.getDescription());
        apiDocument.setDoc(api.getDoc());
        apiDocument.setPublisherId(api.getPublisher().getId());
        apiDocument.setPublisher(ApiPublisher.builder()
                .businessType(api.getPublisher().getBusinessType().toString())
                .build());
        operations.save(apiDocument);
        return api;
    }

    @Override
    public void delete(Integer id) {
        apiRepository.deleteById(id);
    }
}
