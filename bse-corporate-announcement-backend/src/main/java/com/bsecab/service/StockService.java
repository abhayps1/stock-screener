package com.bsecab.service;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsecab.entity.Stock;
import com.bsecab.repository.StockRepository;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> fetchAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        logger.info("Fetched {} stocks from the database.", stocks.size());
        return stocks;
    }

    public void saveStock(String stockName) {
        OkHttpClient client = new OkHttpClient();

        try {
            // Step 1: First request to NSE homepage to get cookies
            Request homepageRequest = new Request.Builder()
                    .url("https://www.nseindia.com/market-data/new-stock-exchange-listings-today")
                    .header("User-Agent", "Mozilla/5.0").header("Accept", "text/html").build();

            Response homepageResponse = client.newCall(homepageRequest).execute();

            // Extract cookies from homepage response
            List<String> cookies = homepageResponse.headers("Set-Cookie");
            StringBuilder cookieHeader = new StringBuilder();
            for (String cookie : cookies) {
                cookieHeader.append(cookie.split(";", 2)[0]).append("; ");
            }
            homepageResponse.close(); // Close the response to free resources

            logger.debug("Cookies extracted for NSE request: {}", cookieHeader);

            Request apiRequest = new Request.Builder()
                    .url("https://www.nseindia.com/api/quote-equity?symbol=" + stockName)
                    .header("User-Agent", "Mozilla/5.0").header("Accept", "application/json")
                    .header("Referer", "https://www.nseindia.com/")
                    .header("Cookie", cookieHeader.toString()).build();

            Response apiResponse = client.newCall(apiRequest).execute();
            logger.info("NSE API response code for {}: {}", stockName, apiResponse.code());

            if (apiResponse.isSuccessful()) {
                try {
                    String responseBody = apiResponse.body().string();
                    JSONObject data = new JSONObject(responseBody);

                    // Check for error or missing info
                    if (data.has("info")) {
                        String companyName = data.getJSONObject("info").getString("companyName");
                        String companyShortUrl = slugify(companyName);
                        logger.info("Short name for company '{}': {}", companyName, companyShortUrl);
                    } else if (data.has("message")) {
                        logger.warn("API Error Message for {}: {}", stockName, data.getString("message"));
                    } else {
                        logger.warn("Unexpected response format for {}: {}", stockName, responseBody);
                    }
                } catch (Exception e) {
                    logger.error("Error parsing JSON for {}: {}", stockName, e.getMessage());
                }
            } else {
                logger.error("Failed to fetch data for {}. Status code: {}", stockName, apiResponse.code());
            }
            apiResponse.close(); // Always close the response

            // stockRepository.save(stock);
        } catch (Exception e) {
            logger.error("An error occurred while making HTTP requests for {}: {}", stockName, e.getMessage(), e);
        }
    }

    // Simple slugify method: lowercase, replace "limited" with "ltd", and replace spaces with hyphens
    private static String slugify(String input) {
        String processed = input.toLowerCase().replaceAll("limited", "ltd");
        return processed.replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "").replaceAll("^-", "");
    }
}
