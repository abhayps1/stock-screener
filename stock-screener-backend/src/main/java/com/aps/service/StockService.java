package com.aps.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
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
import com.aps.util.StockUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockUtility stockUtility;

    public List<Stock> fetchAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        logger.info("Fetched {} stocks from the database.", stocks.size());
        return stocks;
    }

    public void saveStock(String referenceUrl) {
        String[] referenceUrlBreakDown = referenceUrl.split("/");
        String growwUrl = "https://groww.in/stocks/" + referenceUrlBreakDown[2];
        String trendlyneUrl = "https://trendlyne.com/equity/" + referenceUrlBreakDown[3] + "/"
                + referenceUrlBreakDown[2];
        String screenerUrl = "https://www.screener.in/company/" + referenceUrlBreakDown[4] + "/consolidated";

        String indicatorString = stockUtility.getIndicatorData(trendlyneUrl);

        Stock stock = new Stock();
        stock.setStockName(referenceUrlBreakDown[2]);
        stock.setGrowwUrl(growwUrl);
        stock.setScreenerUrl(screenerUrl);
        stock.setTrendlyneUrl(trendlyneUrl);
        stock.setIndicatorData(indicatorString);
        stockRepository.save(stock);
    }

    public void updateIndicatorData() {
        List<String> urls = stockRepository.getAllTrendlyneUrls();
        logger.info("Total Trendlyne URLs to update: {}", urls.size());
        
        for (String trendlyneUrl : urls) {
            String indicatorData = stockUtility.getIndicatorData(trendlyneUrl);
            stockRepository.updateIndicatorData(trendlyneUrl, indicatorData);
            logger.info("Updated indicator data for URL: {}", trendlyneUrl);
        }
    }

    public String searchStock(String companyTerm) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.bseindia.com")
                .addPathSegments("Msource/1D/getQouteSearch.aspx")
                .addQueryParameter("Type", "EQ")
                .addQueryParameter("text", companyTerm)
                .addQueryParameter("flag", "site")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("Referer", "https://www.bseindia.com/")
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("sec-ch-ua", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {

            return "<h6>Request failed</h6>";
        }

    }

    public JSONObject fetchCompanyResults() {
        // Implementation for fetching company results
        // This method will interact with the StockService to get stock data
        // and process it accordingly.

        try {
            OkHttpClient client = new OkHttpClient();

            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String threeDaysBefore = LocalDate.now().minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE);

            String url = "https://groww.in/v1/api/stocks_data/equity_feature/v2/corporate_action/event?from="
                    + threeDaysBefore + "&to=" + today;

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
                for (int i = 0; i < 1; i++) {
                    JSONObject event = events.getJSONObject(i);
                    if ("RESULTS".equals(event.optString("type"))) {
                        String searchId = event.optString("searchId");
                        JSONObject pill = event.optJSONObject("corporateEventPillDto");
                        String primaryDate = pill != null ? pill.optString("primaryDate") : "";
                        // System.out.println("searchId: " + searchId + ", primaryDate: " +
                        // primaryDate);

                        getFinancialStatement(searchId);
                    }
                }
            }
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getFinancialStatement(String searchId) {
        String growwUrl = "https://groww.in/stocks/" + searchId;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(growwUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseData = response.body().string();

                // Parse HTML and extract JSON from __NEXT_DATA__ script tag
                Document doc = Jsoup.parse(responseData);
                Element nextDataScript = doc.getElementById("__NEXT_DATA__");
                if (nextDataScript != null) {
                    String jsonData = nextDataScript.html();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(jsonData);
                    JsonNode financialStatement = root
                            .path("props")
                            .path("pageProps")
                            .path("stockData")
                            .path("financialStatement");

                    if (financialStatement.isMissingNode()) {
                        System.out.println("financialStatement not found.");
                    } else {
                        System.out.println(financialStatement.toPrettyString());
                    }
                } else {
                    System.out.println("__NEXT_DATA__ script tag not found.");
                }
            } else {
                System.out.println("Request failed: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
