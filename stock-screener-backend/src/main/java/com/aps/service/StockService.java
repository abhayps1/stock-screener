package com.aps.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aps.dto.SearchedStockDto;
import com.aps.entity.Results;
import com.aps.entity.Stock;
import com.aps.repository.ResultsRepository;
import com.aps.repository.SearchingRepository;
import com.aps.repository.StockRepository;
import com.aps.util.StockUtility;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ResultsRepository resultsRepository;

    @Autowired
    private StockUtility stockUtility;

    @Autowired
    private SearchingRepository searchingRepository;

    public List<SearchedStockDto> searchStock(String companyTerm) {
        if (companyTerm == null || companyTerm.trim().isEmpty()) {
            return List.of();
        }
        return searchingRepository.searchStock(companyTerm);
    }

    public List<Stock> fetchAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        logger.info("Fetched {} stocks from the database.", stocks.size());
        return stocks;
    }

    public String saveStock(SearchedStockDto searchedStockDto) {
        // Check if stock already exists in the database
        if (stockRepository.existsById(searchedStockDto.getSymbol())) {
            return searchedStockDto.getName() + " already exists in the database.";
        }
        String securityCode = searchedStockDto.getSecurityCode();
        String symbol = searchedStockDto.getSymbol();
        String name = searchedStockDto.getName();
        String endpoint = searchedStockDto.getEndpoint();

        List<String> urls = stockUtility.createUrls(symbol.toLowerCase(), securityCode, endpoint);
        String growwUrl = urls.get(0);
        String screenerUrl = urls.get(1);
        String trendlyneUrl = urls.get(2);

        String trendlyneUniqueId = stockUtility.getTrendlyneUniqueId(trendlyneUrl);
        HashMap<String, String> indicatorMap = stockUtility.fetchIndicatorsMap(trendlyneUniqueId);
        String indicatorString = stockUtility.mapToString(indicatorMap);

        Stock stock = new Stock();
        stock.setName(name);
        stock.setSymbol(symbol);
        stock.setSecurityCode(securityCode);
        stock.setGrowwUrl(growwUrl);
        stock.setScreenerUrl(screenerUrl);
        stock.setTrendlyneUrl(trendlyneUrl);
        stock.setIndicatorData(indicatorString);
        stock.setTrendlyneUniqueId(trendlyneUniqueId);
        stock.setEndpoint(endpoint);

        stockRepository.save(stock);
        return name + " is saved successfully";
    }

    public void updateIndicatorData() {
        List<String> trendlyneUniqueIds = stockRepository.getAllTrendlyneUniqueId();
        logger.info("Updating indicator data for {} stocks", trendlyneUniqueIds.size());

        for (String trendlyneUniqueId : trendlyneUniqueIds) {
            HashMap<String, String> indicatorMap = stockUtility.fetchIndicatorsMap(trendlyneUniqueId);
            String indicatorString = stockUtility.mapToString(indicatorMap);
            stockRepository.updateIndicatorData(trendlyneUniqueId, indicatorString);
            logger.info("Updated indicator data for trendlyneUniqueId: {}", trendlyneUniqueId);
        }
        logger.info("Indicator data update completed.");
    }

    public String fetchResults() {
        try {
            OkHttpClient client = new OkHttpClient();

            String dateFrom = LocalDate.now().minusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dateTo = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            String url = "https://groww.in/v1/api/stocks_data/equity_feature/v2/corporate_action/event?from="
                    + dateFrom + "&to=" + dateTo;

            // Step 1: Make HTTP GET request
            Request request = new Request.Builder().url(url)
                    .header("User-Agent", "Mozilla/5.0").build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Parse JSON and get announcementEvents
            JSONObject json = new JSONObject(responseBody);

            // Loop through only exdateEvents array for RESULTS type events
            String arrayName = "exdateEvents";
            if (json.has(arrayName)) {
                JSONArray events = json.getJSONArray(arrayName);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    if ("RESULTS".equals(event.optString("type"))) {
                        String searchId = event.optString("searchId");
                        JSONObject pill = event.optJSONObject("corporateEventPillDto");
                        String resultDate = pill != null ? pill.optString("primaryDate") : "";
                        JsonNode financialData = stockUtility.getStockFinancialStatement(searchId);
                        if (financialData == null) {
                            continue;
                        }
                        Results result = stockUtility.formatAndSaveData(financialData, searchId, resultDate);
                        if(result != null)
                            resultsRepository.save(result);
                    }
                }
            }
            return "Results is added to db successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Results> getResults() {
        return resultsRepository.findAll();
    }

}
