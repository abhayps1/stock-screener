package com.aps.dto;

import lombok.Data;

@Data
public class AddStockRequest {
    private String stockName;
    private String stockCategory;
    // getters and setters
}
