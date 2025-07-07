package com.aps.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stocks")
@Data
public class Stock {

	@Id
	@Column(name = "stock_name", nullable = false, unique = true)
	private String stockName;

	private String stockCategory;

	private String growwUrl;

	private String screenerUrl;

	private String trendlyneUrl;

}
