package com.aps.service;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aps.entity.Stock;
import com.aps.repository.StockRepository;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;

    private String cookieHeader = null; // Store cookies here

    private void generateCookies() {
        System.out.println("Cookie header generation started");
        OkHttpClient client = new OkHttpClient();
        try {
            Request homepageRequest = new Request.Builder()
                    .url("https://www.nseindia.com/market-data/new-stock-exchange-listings-today")
                    .header("User-Agent", "Mozilla/5.0").header("Accept", "text/html").build();

            Response homepageResponse = client.newCall(homepageRequest).execute();
            List<String> cookies = homepageResponse.headers("Set-Cookie");
            StringBuilder cookieBuilder = new StringBuilder();
            for (String cookie : cookies) {
                cookieBuilder.append(cookie.split(";", 2)[0]).append("; ");
            }
            homepageResponse.close();
            cookieHeader = cookieBuilder.toString();
            logger.debug("Cookies generated for NSE request: {}", cookieHeader);
        } catch (Exception e) {
            logger.error("Error generating cookies: {}", e.getMessage(), e);
        }
    }

    public List<Stock> fetchAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        logger.info("Fetched {} stocks from the database.", stocks.size());
        return stocks;
    }

    public void saveStock(String stockSymbol, String stockCategory) {
        OkHttpClient client = new OkHttpClient();

        // Generate cookies if not already present
        if (cookieHeader == null) {
            generateCookies();
        }

        try {
            Request apiRequest = new Request.Builder()
                    .url("https://www.nseindia.com/api/quote-equity?symbol=" + stockSymbol)
                    .header("User-Agent", "Mozilla/5.0").header("Accept", "application/json")
                    .header("Referer", "https://www.nseindia.com/").header("Cookie", cookieHeader).build();

            Response apiResponse = client.newCall(apiRequest).execute();
            logger.info("NSE API response code for {}: {}", stockSymbol, apiResponse.code());

            if (apiResponse.isSuccessful()) {
                try {
                    String responseBody = apiResponse.body().string();
                    JSONObject data = new JSONObject(responseBody);

                    // Check for error or missing info
                    if (data.has("info")) {
                        String companyName = data.getJSONObject("info").getString("companyName");
                        String companyShortName = slugify(companyName);

                        String growwUrl = "https://groww.in/stocks/" + companyShortName;
                        String trendlyneUrl = "https://trendlyne.com/equity/" + stockSymbol + "/" + companyShortName + "/";
                        String screenerUrl = "https://www.screener.in/company/" + stockSymbol + "/consolidated";

                        Stock stock = new Stock();
                        stock.setSymbol(stockSymbol);
                        stock.setCompanyName(companyName);
                        stock.setCategory(stockCategory);
                        stock.setGrowwUrl(growwUrl);
                        stock.setTrendlyneUrl(trendlyneUrl);
                        stock.setScreenerUrl(screenerUrl);

                        stockRepository.save(stock);

                        logger.info("Short name for company '{}': {}", companyName, companyShortName);
                        logger.info("Stock saved: {}", stock);
                    } else if (data.has("message")) {
                        logger.warn("API Error Message for {}: {}", stockSymbol, data.getString("message"));
                    } else {
                        logger.warn("Unexpected response format for {}: {}", stockSymbol, responseBody);
                    }
                } catch (Exception e) {
                    logger.error("Error parsing JSON for {}: {}", stockSymbol, e.getMessage());
                }
            } else {
                logger.error("Failed to fetch data for {}. Status code: {}", stockSymbol, apiResponse.code());
            }
            apiResponse.close(); // Always close the response

            // stockRepository.save(stock);
        } catch (Exception e) {
            logger.error("An error occurred while making HTTP requests for {}: {}", stockSymbol, e.getMessage(), e);
        }
    }

    // Simple slugify method: lowercase, replace "limited" with "ltd", and replace
    // spaces with hyphens
    private static String slugify(String input) {
        String processed = input.toLowerCase().replaceAll("limited", "ltd");
        return processed.replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "").replaceAll("^-", "");
    }
}
