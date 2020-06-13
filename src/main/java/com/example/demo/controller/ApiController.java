package com.example.demo.controller;

import com.example.demo.dto.ApiDTO;
import com.example.demo.dto.ApiQueryDTO;
import com.example.demo.dto.ApiRequestDTO;
import com.example.demo.entity.Api;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.mapper.ApiMapper;
import com.example.demo.service.ApiService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/apis")
public class ApiController {

    @Autowired
    private ApiMapper mapper;

    @Autowired
    private ApiService service;

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Return Api information")
    @PreAuthorize("hasAnyAuthority('SCOPE_api,SCOPE_api:read')")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Get Apis successfully", response = ApiDTO.class)})
    public ResponseEntity<ApiQueryDTO> getApis(@RequestParam(value = "term", required = false) String term,
                                               @RequestParam(value = "publisherId", required = false) Integer publisherId,
                                               @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        ApiQueryDTO dto = service.queryDTO(term, publisherId, page, size);
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SCOPE_api,SCOPE_api:read')")
    @ApiOperation("Return Api information by id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Get Api successfully", response = ApiDTO.class)})
    public ResponseEntity<ApiDTO> getApi(@PathVariable(value = "id") Integer id) throws ForbiddenException {
        Api api = service.get(id);
        ApiDTO dto = mapper.mapToDto(api);
        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SCOPE_api')")
    @ApiOperation("Create new Api")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Create Api successfully", response = ApiDTO.class)})
    public ResponseEntity<ApiDTO> createApi(@RequestBody ApiRequestDTO dto) throws ForbiddenException {
        Api api = service.create(dto);
        ApiDTO createdApi = mapper.mapToDto(api);
        return new ResponseEntity<>(createdApi, new HttpHeaders(), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SCOPE_api')")
    @ApiOperation("Update Api")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Update Api successfully", response = ApiDTO.class)})
    public ResponseEntity<ApiDTO> updateApi(@PathVariable(value = "id") Integer id, @RequestBody ApiRequestDTO dto) throws ForbiddenException {
        Api api = service.update(id, dto);
        ApiDTO updatedApi = mapper.mapToDto(api);
        return new ResponseEntity<>(updatedApi, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SCOPE_api')")
    @ApiOperation("Delete Api")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Delete Api successfully")})
    public ResponseEntity<Void> deleteApi(@PathVariable(value = "id") Integer id) throws ForbiddenException {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
