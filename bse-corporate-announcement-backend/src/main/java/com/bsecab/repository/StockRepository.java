package com.bsecab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsecab.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    // Custom query methods can be defined here if needed
    // For example, to find a stock by its name:
    // Optional<Stock> findByStockName(String stockName);

}
