package com.aps.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aps.dto.AddStockRequest;
import com.aps.entity.Stock;
import com.aps.service.CompanyResultCalendarService;
import com.aps.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockController {

	@Autowired
	private StockService stockService;

	@Autowired
	private CompanyResultCalendarService companyResultCalendar;

	// Get all stocks
	@GetMapping("/allStocks")
	public ResponseEntity<?> getAllStocks() {
		List<Stock> stocks = stockService.fetchAllStocks();
		if (stocks == null || stocks.isEmpty()) {
			// Return 400 Bad Request if no stocks found
			return ResponseEntity.status(400).body("No stocks are found");
		}
		return ResponseEntity.ok(stocks);
	}

	@PostMapping("/add")
	public ResponseEntity<?> addStock(@RequestBody AddStockRequest request) {
		if (request.getSymbol() == null || request.getSymbol().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Stock name is mandatory.");
		}
		stockService.saveStock(request.getSymbol(), request.getCategory());
		return ResponseEntity.ok("Stock added successfully.");
	}

	@GetMapping("/calendar")
	public void getMethodName() {
		// This method is a placeholder for the calendar functionality.
		// It can be used to trigger the fetching of company results.
		System.out.println("Fetching company results from calendar...");
		companyResultCalendar.fetchCompanyResults();
	}
	
}
