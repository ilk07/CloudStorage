package com.hw.cloudstorage.model.entity;

import com.hw.cloudstorage.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreationTimestamp
    @Column(name = "created")
    private Date created;


    @UpdateTimestamp
    @Column(name = "updated")
    private Date updated;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

}
