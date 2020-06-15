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
import com.example.demo.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ApiServiceImpl implements ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private ApiDao apiDao;

    @Override
    public ApiQueryDTO queryDTO(String term, Integer publisherId, Integer page, Integer size, User user) {
        List<Integer> queryPublisherIds = user.getPublisherIds();
        if (publisherId != null) {
            queryPublisherIds = queryPublisherIds.contains(publisherId) ? Collections.singletonList(publisherId) : null;
        }
        return apiDao.queryByPublisherIds(queryPublisherIds, term, page, size);
    }

    @Override
    public Api get(Integer id, User user) throws DemoException {
        Api api = apiDao.getById(id);
        validateReadAccess(user, api.getPublisher());
        return api;
    }

    @Override
    public Api create(ApiRequestDTO dto, User user) throws DemoException {
        return doUpsert(null, dto, user);
    }

    @Override
    public Api update(Integer id, ApiRequestDTO dto, User user) throws DemoException {
        return doUpsert(id, dto, user);
    }

    private Api doUpsert(Integer id, ApiRequestDTO dto, User user) throws DemoException {
        Publisher publisher = publisherService.getById(dto.getPublisherId());
        validateWriteAccess(user, publisher);
        Api api = (id == null) ? new Api() : get(id, user);
        api.setCreatedOn((id == null) ? new Date() : api.getCreatedOn());
        api.setUpdatedOn(new Date());
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setDoc(dto.getDoc());
        api.setPublisher(publisher);

        return apiDao.upsert(api);
    }

    @Override
    public void delete(Integer id, User user) throws DemoException {
        Api api = apiDao.getById(id);
        validateWriteAccess(user, api.getPublisher());
        apiDao.delete(id);
    }

    private void validateReadAccess(User user, Publisher publisher) throws ForbiddenException {
        if (BusinessType.PRIVATE.equals(publisher.getBusinessType()) && !user.getPublisherIds().contains(publisher.getId())) {
            LOGGER.warn("User {} doesn’t have permission to view {}", user.getUserId(), publisher.getId());
            throw new ForbiddenException(ErrorCode.PUBLISHER_UNAUTHORIZED.toString(), "User doesn’t have permission to view publisher");
        }
    }

    private void validateWriteAccess(User user, Publisher publisher) throws ForbiddenException {
        if (!user.getPublisherIds().contains(publisher.getId())) {
            LOGGER.warn("User {} doesn’t belong to given publisher {}", user.getUserId(), publisher.getId());
            throw new ForbiddenException(ErrorCode.PUBLISHER_UNAUTHORIZED.toString(), "User doesn’t belong to given publisher");
        }
    }
}
