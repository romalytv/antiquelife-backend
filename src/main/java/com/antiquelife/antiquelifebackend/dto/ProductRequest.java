package com.antiquelife.antiquelifebackend.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private String status;
    private String epoch;
    private String origin;
    private String image_path;
    private Long categoryId; // Тут ми приймаємо просто ID

}