package com.aps.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "results")
@Data
public class Results {

    @Id
	@Column(nullable = false, unique = true)
	private String stockName;
    // private String quarterlyRevenue;
    // private String quarterlyProfit;
    // private String yearlyRevenue;
    // private String yearlyProfit;
    // private String yearlyNetworth;
    private Date resultDate;
    private String latestQuarter;
    private double quarterlyRevenueCngPrcnt;
    private double quarterlyProfitCngPrcnt;
    private double yearlyRevenueCngPrcnt;
    private double yearlyProfitCngPrcnt;
    private double yearlyNetworthCngPrcnt;

    private String growwUrl;

	private String screenerUrl;

	private String trendlyneUrl;
    
}
