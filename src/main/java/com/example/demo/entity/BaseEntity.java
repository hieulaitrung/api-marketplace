package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    /** When the object was created. */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = true)
    private Date createdOn;

    /** When the object was last updated. */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "updated_on", nullable = true)
    private Date updatedOn;
}
