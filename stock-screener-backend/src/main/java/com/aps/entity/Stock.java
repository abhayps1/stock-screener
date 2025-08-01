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
	@Column(nullable = false, unique = true)
	private String symbol;

	private String companyName;

	private String category;

	private String growwUrl;

	private String screenerUrl;

	private String trendlyneUrl;

	

}
