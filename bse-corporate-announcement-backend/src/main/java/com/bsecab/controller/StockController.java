package com.bsecab.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bsecab.entity.Stock;
import com.bsecab.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

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
    public ResponseEntity<?> addStock(@RequestParam String stockName) {
        if (stockName == null || stockName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Stock name is mandatory.");
        }
        stockService.saveStock(stockName);
        return ResponseEntity.ok("Stock added successfully.");
    }
}
