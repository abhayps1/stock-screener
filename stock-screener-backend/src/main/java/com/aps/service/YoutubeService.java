package com.aps.service;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aps.entity.YoutubeChannel;
import com.aps.entity.YoutubeVideo;
import com.aps.repository.YoutubeChannelRepository;
import com.aps.repository.YoutubeVideoRepository;

@Service
public class YoutubeService {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeService.class);

    @Autowired
    private YoutubeChannelRepository youtubeChannelRepository;

    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    private String youtube_api_key = "AIzaSyAk-1LHcADRQPqmmwGOxgUxknxWpjm0s9A";

    public YoutubeChannel fetchChannelId(String handle) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            HttpUrl url = HttpUrl.parse("https://www.googleapis.com/youtube/v3/channels").newBuilder()
                    .addQueryParameter("part", "id,snippet")
                    .addQueryParameter("forHandle", handle)
                    .addQueryParameter("key", youtube_api_key)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            int statusCode = response.code();
            logger.info("Response Status Code: " + statusCode);

            String contentType = response.header("Content-Type");
            logger.info("Content-Type: " + contentType);

            JSONObject jsonResponse = new JSONObject(response.body().string());

            if (jsonResponse.getJSONObject("pageInfo").getInt("totalResults") > 0) {
                String channelId = jsonResponse.getJSONArray("items").getJSONObject(0).getString("id");
                logger.info("Channel ID: " + channelId);
                String title = jsonResponse.getJSONArray("items").getJSONObject(0).getJSONObject("snippet")
                        .getString("title");
                logger.info("Channel Title: " + title);
                YoutubeChannel channel = new YoutubeChannel(channelId, handle, title, null);
                youtubeChannelRepository.save(channel);
                return channel;
            } else {
                return new YoutubeChannel("Channel ID not found", handle, "Title not found", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new YoutubeChannel("Error occurred: " + e.getMessage(), handle, "", null);
        }
    }

    public List<YoutubeVideo> fetchVideosByChannelId(String channelId) {
        try {
            // ➤ Fetch the channel entity (required for relationship)
            YoutubeChannel channel = youtubeChannelRepository.findById(channelId)
                    .orElseThrow(() -> new RuntimeException("Channel not found"));

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            HttpUrl url = HttpUrl.parse("https://www.googleapis.com/youtube/v3/search").newBuilder()
                    .addQueryParameter("part", "snippet")
                    .addQueryParameter("channelId", channelId)
                    .addQueryParameter("maxResults", "25")
                    .addQueryParameter("order", "date")
                    .addQueryParameter("key", youtube_api_key)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            List<YoutubeVideo> videosList = new ArrayList<>();
            JSONArray items = jsonResponse.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject idObj = item.getJSONObject("id");
                if (!idObj.getString("kind").equals("youtube#video")) {
                    continue;
                }
                String videoId = idObj.getString("videoId");
                String title = item.getJSONObject("snippet").getString("title");
                YoutubeVideo youtubeVideo = new YoutubeVideo(videoId, title, channel); // Set channel FK
                videosList.add(youtubeVideo);
                youtubeVideoRepository.save(youtubeVideo); // Persist video record
            }
            return videosList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<YoutubeVideo> getChannelVideos(String channelId) {
        return youtubeVideoRepository.findByChannel_ChannelId(channelId);
    }

}
