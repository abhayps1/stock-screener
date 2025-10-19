package com.aps.dto;

public class AllStocksDto {
    public String securityCode;
    public String symbol;
    public String name;
    public String listingDate;

    // Constructors, getters, and setters can be added as needed
    public AllStocksDto(String securityCode, String symbol, String name, String listingDate) {
        this.securityCode = securityCode;
        this.symbol = symbol;
        this.name = name;
        this.listingDate = listingDate;
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

    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    public String toString() {
        return "AllStocksDto{" +
                "securityCode='" + securityCode + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", listingDate='" + listingDate + '\'' +
                '}';
    }
}
