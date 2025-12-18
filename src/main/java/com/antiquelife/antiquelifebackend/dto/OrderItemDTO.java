package com.antiquelife.antiquelifebackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemDTO {
    private UUID productId;
    private Integer quantity;
}