package com.aps.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CompanyResultCalendarService {

    private static final String GROWW_BASE_URL = "https://groww.in";

    public JSONObject fetchCompanyResults() {
        // Implementation for fetching company results
        // This method will interact with the StockService to get stock data
        // and process it accordingly.

        try {
            OkHttpClient client = new OkHttpClient();

            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String threeDaysBefore = LocalDate.now().minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE);

            String url = GROWW_BASE_URL + "/v1/api/stocks_data/equity_feature/v2/corporate_action/event?from=" 
                + threeDaysBefore + "&to=" + today;

            // Step 1: Make HTTP GET request
            Request request = new Request.Builder().url(url)
                    .header("User-Agent", "Mozilla/5.0").build();

            Response response = client.newCall(request).execute();

            // System.out.println("Response Code: " + response.code());
            // if (!response.isSuccessful()) {
            //     System.out.println("Failed to fetch data: " + response.message());
            //     return;
            // }

            String responseBody = response.body().string();
            // System.out.println("Response Body: " + responseBody);

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
                        // System.out.println("searchId: " + searchId + ", primaryDate: " + primaryDate);
                        
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
        String growwUrl = GROWW_BASE_URL+"/stocks/"+searchId;
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
