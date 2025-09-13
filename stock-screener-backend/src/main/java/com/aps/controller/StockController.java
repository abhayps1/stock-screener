package com.aps.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aps.dto.SearchedStockDto;
import com.aps.entity.Results;
import com.aps.entity.Stock;
import com.aps.service.StockService;


@RestController
@RequestMapping("/api/stock")
public class StockController {

	private static final Logger logger = LoggerFactory.getLogger(StockController.class);

	@Autowired
	private StockService stockService;

	// Get all stocks
	@GetMapping("/allStocks")
	public ResponseEntity<?> getAllStocks() {
		logger.info("Fetching all stocks from database");
		try {
			List<Stock> stocks = stockService.fetchAllStocks();
			if (stocks == null || stocks.isEmpty()) {
				logger.warn("No stocks found in database");
				// Return 400 Bad Request if no stocks found
				return ResponseEntity.status(400).body("No stocks are found");
			}
			logger.info("Successfully fetched {} stocks", stocks.size());
			return ResponseEntity.ok(stocks);
		} catch (Exception e) {
			logger.error("Error occurred while fetching stocks: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Internal server error occurred while fetching stocks");
		}
	}

	@PostMapping("/add")
	public String addStock(@RequestBody SearchedStockDto searchedStockDto) {
		return stockService.saveStock(searchedStockDto);
	}

	@PostMapping("/fetchResults")
	public String fetchResults() {
		
		logger.info("Fetching company results from calendar");
		try {
			String message = stockService.fetchResults();
			logger.info("Successfully fetched all results");
			return message;
		} catch (Exception e) {
			logger.error("Error occurred while fetching company results: {}", e.getMessage(), e);
			return "Failed to fetch company results.";
		}
	}


	@GetMapping("/getResults")
	public List<Results> getResults() {
		logger.info("Getting company results from database");
		try {
			List<Results> results = stockService.getResults();
			logger.info("Successfully got all results");
			return results;
		} catch (Exception e) {
			logger.error("Error occurred while getting company results: {}", e.getMessage(), e);
			return null;
		}
	}
	

	@GetMapping("/updateIndicatorData")
	public void updateIndicatorData() {
		logger.info("Updating indicator data for all stocks");
		stockService.updateIndicatorData();
	}

	@GetMapping("/search")
	public List<SearchedStockDto> searchStock(@RequestParam String companyTerm) {
		return stockService.searchStock(companyTerm);
	}

}
