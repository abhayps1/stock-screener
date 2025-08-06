package com.aps.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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

import com.aps.dto.AddStockRequest;
import com.aps.entity.Stock;
import com.aps.service.CompanyResultCalendarService;
import com.aps.service.StockService;


@RestController
@RequestMapping("/api/stock")
public class StockController {

	private static final Logger logger = LoggerFactory.getLogger(StockController.class);

	@Autowired
	private StockService stockService;

	@Autowired
	private CompanyResultCalendarService companyResultCalendar;


	//searchStockData
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
	public ResponseEntity<?> addStock(@RequestBody AddStockRequest request) {
		logger.info("Adding new stock with symbol: {}", request.getSymbol());
		
		if (request.getSymbol() == null || request.getSymbol().trim().isEmpty()) {
			logger.warn("Stock symbol is null or empty");
			return ResponseEntity.badRequest().body("Stock name is mandatory.");
		}
		
		try {
			stockService.saveStock(request.getSymbol(), request.getCategory());
			logger.info("Stock added successfully: {}", request.getSymbol());
			return ResponseEntity.ok("Stock added successfully.");
		} catch (Exception e) {
			logger.error("Error occurred while adding stock {}: {}", request.getSymbol(), e.getMessage(), e);
			return ResponseEntity.status(500).body("Internal server error occurred while adding stock");
		}
	}

	@GetMapping("/calendar")
	public JSONObject getMethodName() {
		// This method is a placeholder for the calendar functionality.
		// It can be used to trigger the fetching of company results.
		logger.info("Fetching company results from calendar");
		try {
			JSONObject json = companyResultCalendar.fetchCompanyResults();
			logger.info("Successfully fetched company results from calendar");
			return json;
		} catch (Exception e) {
			logger.error("Error occurred while fetching company results from calendar: {}", e.getMessage(), e);
			JSONObject errorJson = new JSONObject();
			errorJson.put("error", "Failed to fetch company results");
			return errorJson;
		}
	}

	@GetMapping("/rsi-mfi")
	public ResponseEntity<Map<String, String>> getRsiAndMfiData() {
		logger.info("Fetching RSI and MFI data");
		try {
			Map<String, String> result = stockService.getIndicatorsData();
			if(result.containsKey("error")) {
				logger.warn("Error in RSI/MFI data: {}", result.get("error"));
				return ResponseEntity.status(500).body(result);
			}
			logger.info("Successfully fetched RSI and MFI data");
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			logger.error("Error occurred while fetching RSI and MFI data: {}", e.getMessage(), e);
			Map<String, String> errorResult = Map.of("error", "Internal server error occurred while fetching indicators data");
			return ResponseEntity.status(500).body(errorResult);
		}
	}

	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		logger.info("Health check endpoint called");
		return ResponseEntity.ok("Backend is running successfully!");
	}

}
