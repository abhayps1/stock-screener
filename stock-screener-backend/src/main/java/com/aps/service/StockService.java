package com.aps.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aps.entity.Stock;
import com.aps.repository.StockRepository;
import com.aps.util.StockUtility;
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

        HashMap<String, String> indicators = stockUtility.fetchIndicatorsData(trendlyneUrl);
        ObjectMapper mapper = new ObjectMapper();
        String indicatorString = "";
        try {
            indicatorString = mapper.writeValueAsString(indicators);
        } catch (IOException e) {
            logger.error("Error converting indicators to JSON string: {}", e.getMessage());
        }

        Stock stock = new Stock();
        stock.setStockName(referenceUrlBreakDown[2]);
        stock.setGrowwUrl(growwUrl);
        stock.setScreenerUrl(screenerUrl);
        stock.setTrendlyneUrl(trendlyneUrl);
        stock.setIndicatorData(indicatorString);
        stockRepository.save(stock);
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

}
