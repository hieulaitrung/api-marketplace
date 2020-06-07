package com.example.demo.service;

import com.example.demo.constant.BusinessType;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.elasticsearch.ApiDocument;
import com.example.demo.elasticsearch.ApiPublisher;
import com.example.demo.entity.Api;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.PublisherRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.demo.constant.CacheName.API;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public SearchHits<ApiDocument> getAll(String id, String term, Integer page, Integer size) {
        BoolQueryBuilder builder = boolQuery();
        if (StringUtils.isNotBlank(term)) {
            builder.must(multiMatchQuery(term, "name", "description", "doc"));
        }
        BoolQueryBuilder subBuilder =  boolQuery();
        subBuilder.should(nestedQuery("publisher", matchQuery("publisher.businessType", BusinessType.PUBLIC.name()), ScoreMode.Avg));
        if (StringUtils.isNotBlank(id)) {
            List<String> ids = Arrays.asList(id.split(","));
            ids.forEach(i -> subBuilder.should(matchQuery("publisherId", i)));
        }
        builder.filter(subBuilder);

        Query query = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();
        return operations.search(query, ApiDocument.class);
    }

    @Override
    @Cacheable(value = API, key = "#id", unless = "#result == null")
    public Api get(Integer id) {
        return apiRepository.findById(id).get();
    }

    @Override
    @CachePut(value = API, key = "#result.id")
    public Api create(ApiRequestDTO dto) {
        Api api = new Api();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setCreatedOn(new Date());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherRepository.findById(dto.getPublisherId()).get());
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
    @CachePut(value = API, key = "#id")
    public Api update(Integer id, ApiRequestDTO dto) {
        Api api = get(id);
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setUpdatedOn(new Date());
        api.setPublisher(publisherRepository.findById(dto.getPublisherId()).get());
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
    @CacheEvict(value = API, allEntries=true)
    public void delete(Integer id) {
        apiRepository.deleteById(id);
        operations.delete(id.toString(), ApiDocument.class);
    }
}
