package com.bsecab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsecab.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

}
