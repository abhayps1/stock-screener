package com.bsecab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bsecab.service.StockDataFetchService;

@Controller
@RequestMapping("/api/stocks")
public class StockController {

	@Autowired
	private StockDataFetchService stockDataFetchService;

	@GetMapping
	public ResponseEntity<String> fetchStockDataUsingSymbol(@RequestParam String symbol) {
		String stockData = stockDataFetchService.fetchStockDataUsingSymbol(symbol);
		return ResponseEntity.ok(stockData);
	}
}
