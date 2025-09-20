package com.aps.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stocks")
@Data
public class Stock {
	private String name;

	@Id
	@Column(nullable = false, unique = true)
	private String symbol;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private Watchlist watchlist;

	private String securityCode;

	private String growwUrl;

	private String screenerUrl;

	private String trendlyneUrl;

	private String indicatorData;

	private String trendlyneUniqueId;

	private String endpoint;
}
