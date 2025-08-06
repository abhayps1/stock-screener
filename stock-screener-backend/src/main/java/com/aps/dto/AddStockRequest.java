package com.aps.dto;

import lombok.Data;

@Data
public class AddStockRequest {
    private String symbol;
    private String category;
    // getters and setters
}