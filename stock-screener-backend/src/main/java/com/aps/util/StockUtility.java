package com.aps.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aps.entity.Results;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class StockUtility {

    private static final Logger logger = LoggerFactory.getLogger(StockUtility.class);

    Results results = new Results();

    public String mapToString(HashMap<String, String> indicators) {
        String indicatorString = null;
        if (indicators.containsKey("error")) {
            logger.error("Error fetching indicators data: {}", indicators.get("error"));
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                indicatorString = mapper.writeValueAsString(indicators);
            } catch (IOException e) {
                logger.error("Error converting indicators to JSON string: {}", e.getMessage());
            }
        }
        return indicatorString;
    }

    public String getTrendlyneUniqueId(String trendlyneUrl) {
        HashMap<String, String> indicatorsMap = new HashMap<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(trendlyneUrl).build();
        String trendlyneUniqueId = null;

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseData = response.body().string();

                // Parse HTML and extract data-myid
                Document doc = Jsoup.parse(responseData);
                Element infoCard = doc.selectFirst("div.stock_info_card.LpriceTop");
                if (infoCard != null) {
                    trendlyneUniqueId = infoCard.attr("data-myid");
                } else {
                    logger.error("Unable to get the stock index in trendlyne.");
                }
            } else {
                logger.error("Request failed: {}", response.code());
            }
        } catch (IOException e) {
            logger.error("Exception occurred: {}", e.getMessage());
        }
        return trendlyneUniqueId;
    }

    public HashMap<String, String> fetchIndicatorsMap(String getTrendlyneUniqueId) {

        HashMap<String, String> indicatorsMap = new HashMap<>();
        OkHttpClient client = new OkHttpClient();
        String lazyLoadUrl = "https://trendlyne.com/equity/second-part-lazy-load-v2/" + getTrendlyneUniqueId + "/";

        Request request = new Request.Builder().url(lazyLoadUrl).get().build();

        Response response;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String html = response.body().string();
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

                return indicatorsMap;

            } else {
                indicatorsMap.put("error", "Failed to fetch data: " + response.code());
                return indicatorsMap;
            }
        }

        catch (IOException e) {
            e.printStackTrace();
            indicatorsMap.put("error", "Exception occurred: " + e.getMessage());
        }
        return indicatorsMap;

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

    public Results formatAndSaveData(JsonNode financialData, String searchId, String resultDate) {
        JsonNode quarterlyRevenue = financialData.get(0).get("quarterly");
        JsonNode quarterlyProfit = financialData.get(1).get("quarterly");
        JsonNode yearlyRevenue = financialData.get(0).get("yearly");
        JsonNode yearlyProfit = financialData.get(1).get("yearly");
        JsonNode yearlyNetworth = financialData.get(2).get("yearly");

        double quarterlyRevenueCngPrcnt = getPercentageChange(quarterlyRevenue);
        double quarterlyProfitCngPrcnt = getPercentageChange(quarterlyProfit);
        double yearlyRevenueCngPrcnt = getPercentageChange(yearlyRevenue);
        double yearlyProfitCngPrcnt = getPercentageChange(yearlyProfit);
        double yearlyNetworthCngPrcnt = getPercentageChange(yearlyNetworth);
        String latestQuarter = getLatestQuarter(quarterlyRevenue);

        results.setStockName(searchId);
        results.setQuarterlyRevenueCngPrcnt(quarterlyRevenueCngPrcnt);
        results.setQuarterlyProfitCngPrcnt(quarterlyProfitCngPrcnt);
        results.setYearlyRevenueCngPrcnt(yearlyRevenueCngPrcnt);
        results.setYearlyProfitCngPrcnt(yearlyProfitCngPrcnt);
        results.setYearlyNetworthCngPrcnt(yearlyNetworthCngPrcnt);
        results.setResultDate(resultDate);
        results.setLatestQuarter(latestQuarter);
        return results;
    }

    public double getPercentageChange(JsonNode data) {
        if(data == null || !data.isObject() || data.size() < 2) {
            return 0; // Not enough data to calculate percentage change
        }
        Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
        double lastValue = 0, secondLastValue = 0;
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            // shift values as we iterate
            secondLastValue = lastValue;
            lastValue = entry.getValue().asDouble();
        }

        if(secondLastValue == 0) {
            return 0; // Avoid division by zero
        }
        // calculate percentage change
        double percentageChange = ((lastValue - secondLastValue) / Math.abs(secondLastValue)) * 100;
        return percentageChange;

    }

    public String getLatestQuarter(JsonNode data) {
        if(data == null || !data.isObject() || data.size() < 1) {
            return "N/A"; // Not enough data to determine latest quarter
        }
        Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
        String latestQuarter = "";
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            latestQuarter = entry.getKey(); // Keep updating to get the last key
        }
        return latestQuarter;
    }

}
