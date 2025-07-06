package com.bsecab.service;

import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsecab.entity.Stock;
import com.bsecab.repository.StockRepository;

@Service
public class StockService {

	// private static final Logger logger = LoggerFactory.getLogger(StockDataFetchService.class);

	@Autowired
	private StockRepository stockRepository;

	public List<Stock> fetchAllStocks() {
		List<Stock> stocks = stockRepository.findAll();
		return stocks;
	}

	public void saveStock(Stock stock) {
		stockRepository.save(stock);
	}
}
