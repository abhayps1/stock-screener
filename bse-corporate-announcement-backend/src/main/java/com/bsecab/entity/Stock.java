package com.bsecab.entity;

import java.math.BigDecimal;

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
	@Column(length = 10)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(precision = 10, scale = 2)
	private BigDecimal currentPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal week52High;

	@Column(precision = 10, scale = 2)
	private BigDecimal week52Low;

	@Column(precision = 15, scale = 2)
	private BigDecimal marketCap;

//	@Column(precision = 15, scale = 2)
//	private BigDecimal volume;

	private Double peRatio;

//	private Double dividendYield;

	@Column(precision = 10, scale = 2)
	private BigDecimal bookValue;

//	private LocalDateTime lastUpdated;

}
