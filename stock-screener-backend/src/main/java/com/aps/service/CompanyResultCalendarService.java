package com.aps.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CompanyResultCalendarService {

    private static final String GROWW_BASE_URL = "https://groww.in";

    public void fetchCompanyResults() {
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
            if (!response.isSuccessful()) {
                System.out.println("Failed to fetch data: " + response.message());
                return;
            }

            String responseBody = response.body().string();
            // System.out.println("Response Body: " + responseBody);

            // Write response body to a JSON file in the same directory
            try {
                String filePath = "src/main/java/com/aps/service/response.json";
                Files.write(Paths.get(filePath), responseBody.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Response saved to " + filePath);
            } catch (Exception ex) {
                System.out.println("Failed to write response to file: " + ex.getMessage());
            }

            // Parse JSON and get announcementEvents
            JSONObject json = new JSONObject(responseBody);

            // Loop through both arrays
            String[] eventArrays = {"announcementEvents", "exdateEvents"};
            for (String arrayName : eventArrays) {
                if (!json.has(arrayName)) continue;
                JSONArray events = json.getJSONArray(arrayName);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    if ("RESULTS".equals(event.optString("type"))) {
                        String searchId = event.optString("searchId");
                        JSONObject pill = event.optJSONObject("corporateEventPillDto");
                        String primaryDate = pill != null ? pill.optString("primaryDate") : "";
                        // System.out.println("searchId: " + searchId + ", primaryDate: " + primaryDate);

                        String growwUrl = "https://groww.in/stocks/" + searchId;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStockData(String url) {
        // This method can be used to fetch stock data based on the searchId
        // and process it further as needed.
        System.out.println("Fetching stock data for searchId: " + searchId);
        // Implement the logic to fetch and process stock data here.
    }
}
