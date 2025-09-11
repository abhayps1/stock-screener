package com.aps.dto;

public class SearchedStockDto {
    public String securityCode;
    public String symbol;
    public String name;
    public String endpoint;

    // Constructors, getters, and setters can be added as needed
    public SearchedStockDto(String securityCode, String symbol, String name, String endpoint) {
        this.securityCode = securityCode;
        this.symbol = symbol;
        this.name = name;
        this.endpoint = endpoint;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String toString() {
        return "SearchedStockDto{" +
                "securityCode='" + securityCode + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
