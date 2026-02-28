package com.codexaa.model;

import com.codexaa.domain.StoreStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String description;
    private String storeType;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    @OneToOne
    @JoinColumn(name = "store_admin_id", nullable = false)
    private User storeAdmin;

    @Embedded
    private StoreContact contact;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected  void onCreate(){
        createdAt=LocalDateTime.now();

    }

    @PreUpdate
    protected  void  onUpdate(){
        updatedAt=LocalDateTime.now();
    }
}