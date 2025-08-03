package com.aps.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                        String trendlyneUrl = "https://trendlyne.com/equity/" + stockSymbol + "/" + companyShortName
                                + "/";
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

    public Map<String, String> getIndicatorsData() {

        Map<String, String> indicatorsMap = new HashMap<>();
        String trendlyneURL = "https://trendlyne.com/equity/SHILCTECH/shilchar-technologies-ltd/";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(trendlyneURL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseData = response.body().string();

                // Parse HTML and extract data-myid
                Document doc = Jsoup.parse(responseData);
                Element infoCard = doc.selectFirst("div.stock_info_card.LpriceTop");
                if (infoCard != null) {
                    String myId = infoCard.attr("data-myid");
                    String lazyLoadUrl = "https://trendlyne.com/equity/second-part-lazy-load-v2/" + myId + "/";

                    Request lazyLoadRequest = new Request.Builder().url(lazyLoadUrl).get().build();

                    try (Response lazyLoadResponse = client.newCall(lazyLoadRequest).execute()) {
                        if (lazyLoadResponse.isSuccessful() && lazyLoadResponse.body() != null) {
                            String html = lazyLoadResponse.body().string();
                            Document document = org.jsoup.Jsoup.parse(html);

                            List<String> indicators = Arrays.asList(
                                    "Day RSI",
                                    "Day MACD",
                                    "Day MFI",
                                    "Day MACD Signal Line",
                                    "Day ADX",
                                    "Day ATR",
                                    "Day Commodity Channel Index",
                                    "Day ROC125",
                                    "Day ROC21",
                                    "William");

                            for (String indicator : indicators) {
                                indicatorsMap.put(indicator, extractIndicatorUsingIndicatorName(document, indicator));
                            }
                            System.out.println(indicatorsMap);

                            return indicatorsMap;
                        } else {
                            indicatorsMap.put("error", "Failed to fetch data: " + lazyLoadResponse.code());
                            return indicatorsMap;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        indicatorsMap.put("error", "Exception occurred: " + e.getMessage());
                        return indicatorsMap;
                    }

                } else {
                    indicatorsMap.put("error", "Unable to get the stock index in trendlyne.");
                    return indicatorsMap;
                }
            } else {
                indicatorsMap.put("error", "Request failed: " + response.code());
                return indicatorsMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            indicatorsMap.put("error", "Exception occurred: " + e.getMessage());
            return indicatorsMap;
        }

    }

    public String extractIndicatorUsingIndicatorName(Document document, String indicator) {

        String regex = ":matchesOwn(^" + indicator + "$)";
        Element current = document.selectFirst(regex);
        if (current != null) {
            // Get the next sibling element
            Element next = current.nextElementSibling();
            return next.text();
        }
        return indicator + " data is not found";
    }

}
