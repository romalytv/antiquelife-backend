package com.antiquelife.antiquelifebackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private String status;
    private String epoch;
    private String origin;
    private List<String> imageUrls;
    private Long categoryId; // Тут ми приймаємо просто ID
    private Double quantity;

}