package com.antiquelife.antiquelifebackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIRequest {
    private List<String> images;
}