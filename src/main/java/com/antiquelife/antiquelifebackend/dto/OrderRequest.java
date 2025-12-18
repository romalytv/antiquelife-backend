package com.antiquelife.antiquelifebackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private CustomerDTO customer;
    private List<OrderItemDTO> items;
}
