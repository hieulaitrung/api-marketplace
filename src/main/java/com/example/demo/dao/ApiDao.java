package com.example.demo.dao;

import com.example.demo.constant.BusinessType;
import com.example.demo.constant.ErrorCode;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.elasticsearch.ApiDocument;
import com.example.demo.elasticsearch.ApiPublisher;
import com.example.demo.entity.Api;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ApiMapper;
import com.example.demo.repository.ApiRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.example.demo.constant.CacheName.API;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Repository
public class ApiDao implements GenericDao<Api, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDao.class);

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    private ApiMapper mapper;

    @Override
    @Cacheable(value = API, key = "#id", unless = "#result == null")
    public Api getById(Integer id) throws ResourceNotFoundException {
        return apiRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.API_NOT_FOUND.toString(), "API not found " + id));
    }

    @Override
    @CachePut(value = API, key = "#result.id")
    public Api upsert(Api api) {
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
    @CacheEvict(value = API, key = "#id")
    public void delete(Integer id) {
        apiRepository.deleteById(id);
        operations.delete(id.toString(), ApiDocument.class);
    }

    public ApiQueryDTO queryByPublisherIds(List<Integer> publisherIds, String term, Integer page, Integer size) {
        BoolQueryBuilder builder = boolQuery();
        BoolQueryBuilder filter =  boolQuery();
        builder.filter(filter);
        
        if (CollectionUtils.isEmpty(publisherIds)) {
            return ApiQueryDTO.none();
        } else {
            publisherIds.forEach(i -> filter.should(matchQuery("publisherId", i)));
            filter.should(nestedQuery("publisher", matchQuery("publisher.businessType", BusinessType.PUBLIC.name()), ScoreMode.Avg));
        }
        if (StringUtils.isNotBlank(term)) {
            builder.must(multiMatchQuery(term, "name", "description", "doc"));
        }

        Query query = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .withPageable(PageRequest.of(page, size))
                .build();
        SearchHits<ApiDocument> searchHits = operations.search(query, ApiDocument.class);
        return mapper.mapToDto(searchHits);
    }
}
