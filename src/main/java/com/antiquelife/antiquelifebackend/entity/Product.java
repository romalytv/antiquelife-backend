package com.antiquelife.antiquelifebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID product_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String epoch;

    @Column(nullable = false)
    private String origin;

    @Column
    private String image_path;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @Column
    private Double quantity;


}
