package com.aps.controller;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aps.entity.Stock;
import com.aps.service.StockService;


@RestController
@RequestMapping("/api/stock")
public class StockController {

	private static final Logger logger = LoggerFactory.getLogger(StockController.class);

	@Autowired
	private StockService stockService;

	@GetMapping("/search")
	public ResponseEntity<String> searchStock(@RequestParam String companyTerm) {
		logger.info("Searching for the term: {}", companyTerm);
		
		if (companyTerm == null || companyTerm.trim().isEmpty()) {
			logger.warn("Stock name is null or empty");
			return ResponseEntity.badRequest().body("Stock name is mandatory.");
		}
		
		String result = stockService.searchStock(companyTerm);
		// logger.info("Search completed successfully for term: {}", companyTerm);
		return ResponseEntity.ok(result);
	}

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
	public void addStock(@RequestParam String referenceUrl) {
		logger.info("The things are working ");
		System.out.println("Reference URL: " + referenceUrl);
		stockService.saveStock(referenceUrl);
	}

	@GetMapping("/calendar")
	public JSONObject getMethodName() {
		
		logger.info("Fetching company results from calendar");
		try {
			JSONObject json = stockService.fetchCompanyResults();
			logger.info("Successfully fetched company results from calendar");
			return json;
		} catch (Exception e) {
			logger.error("Error occurred while fetching company results from calendar: {}", e.getMessage(), e);
			JSONObject errorJson = new JSONObject();
			errorJson.put("error", "Failed to fetch company results");
			return errorJson;
		}
	}

	@GetMapping("/updateIndicatorData")
	public void updateIndicatorData() {
		logger.info("Updating indicator data for all stocks");
		stockService.updateIndicatorData();
	}

}
