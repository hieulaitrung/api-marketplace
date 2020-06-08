package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;

import static com.example.demo.constant.CacheName.API;

@Entity
@Getter
@Setter
@Table(name = "api")
@ToString
@RedisHash(API)
public class Api extends BaseEntity implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "doc")
    private String doc;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

}
