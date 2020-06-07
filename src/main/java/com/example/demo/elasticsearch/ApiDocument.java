package com.example.demo.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "api", createIndex = false)
@Getter
@Setter
public class ApiDocument {

    @Id
    private Integer id;
    private String name;
    private String description;
    private String doc;
    private Integer publisherId;

    @JsonProperty("publisher")
    @Field( type = FieldType.Nested)
    private ApiPublisher publisher;

}
