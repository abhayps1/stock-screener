package com.aps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aps.entity.Stock;

import jakarta.transaction.Transactional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

	// Custom query methods can be defined here if needed
	// For example, to find a stock by its name:
	// Optional<Stock> findByStockName(String stockName);

	@Query("SELECT s.trendlyneUrl FROM Stock s WHERE s.trendlyneUrl IS NOT NULL AND s.indicatorData IS NOT NULL")
	List<String> getAllTrendlyneUrls();

	@Modifying
    @Transactional
    @Query("UPDATE Stock s SET s.indicatorData = :indicatorData WHERE s.trendlyneUrl = :trendlyneUrl")
	void updateIndicatorData(String trendlyneUrl, String indicatorData);

}
