package com.bsecab.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bsecab.entity.Announcement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class AnnouncementService {

	private final static OkHttpClient httpClient = new OkHttpClient();

	public List<Announcement> fetchLatestAnnouncements() {
		List<Announcement> announcements = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		LocalDate toDate = LocalDate.now();
//		LocalDate prevDate = toDate.minusDays(1);
		LocalDate prevDate = LocalDate.now();

		String strPrevDate = prevDate.format(formatter);
		String strToDate = toDate.format(formatter);

		String baseUrl = "https://api.bseindia.com/BseIndiaAPI/api/AnnSubCategoryGetData/w";

		HttpUrl url = HttpUrl.parse(baseUrl).newBuilder().addQueryParameter("pageno", "1")
				.addQueryParameter("strCat", "Company Update").addQueryParameter("strPrevDate", strPrevDate)
				.addQueryParameter("strScrip", "").addQueryParameter("strSearch", "P")
				.addQueryParameter("strToDate", strToDate).addQueryParameter("strType", "C")
				.addQueryParameter("subcategory", "Award of Order / Receipt of Order").build();

		Request request = new Request.Builder().url(url).get().header("Referer", "https://www.bseindia.com/").build();

		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected HTTP code: " + response);
			}

			String responseBody = response.body().string();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(responseBody);
			JsonNode tableNode = rootNode.path("Table");

			if (tableNode.isArray()) {
				for (JsonNode node : tableNode) {
					Announcement ann = new Announcement();
					ann.setNewsId(node.path("NEWSID").asText());
					ann.setAttachment(node.path("ATTACHMENTNAME").asText());
					ann.setDescription(node.path("HEADLINE").asText());
					ann.setNewsSubmissionDate(node.path("NEWS_DT").asText());
					ann.setCompanyName(node.path("SLONGNAME").asText());
					announcements.add(ann);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return announcements;
	}

}
