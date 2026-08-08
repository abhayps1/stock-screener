package com.aps.service;

import com.aps.entity.Result;
import com.aps.mapper.BeanMapper;
import com.aps.repository.ResultRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    private final ResultRepository resultRepository;
    private final BeanMapper beanMapper;

    public StockService(ResultRepository resultRepository, BeanMapper beanMapper) {
        this.resultRepository = resultRepository;
        this.beanMapper = beanMapper;
    }

    public String fetchResults(String from, String to) {
        long startTime = System.currentTimeMillis();
        try {
            RestClient restClient = RestClient.create();

            String body = restClient
                    .get()
                    .uri("https://groww.in/v1/api/stocks_data/equity_feature/v2/corporate_action/event?from=" + from + "&to=" + to)
                    .retrieve()
                    .body(String.class);
            JSONObject jsonObject = new JSONObject(body);

            String arrayName = "exdateEvents";
            if (jsonObject.has(arrayName)) {
                JSONArray events = jsonObject.getJSONArray(arrayName);
                List<Result> results = new ArrayList<>();
                for(int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    if("RESULTS".equals(event.optString("type"))) {
                        Result result = beanMapper.mapEventToResult(event);
                        results.add(result);
                    }
                }
                resultRepository.saveAll(results);
            }
            long elaspedTime = System.currentTimeMillis() - startTime;
            return "Results fetched and saved successfully in " + elaspedTime + " ms.";
        } catch (Exception e) {
            long elaspedTime = System.currentTimeMillis() - startTime;
            return "Error occurred while fetching results: " + e.getMessage() + " in " + elaspedTime + " ms.";
        }
    }

}
