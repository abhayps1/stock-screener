package com.bsecab.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bsecab.dto.IpoDTO;

@Service
public class IpoService {

	private static final String FULL_URL = "https://webnodejs.investorgain.com/cloud/ipo/list-read";

	public List<IpoDTO> getAllIpos() {
		RestClient restClient = RestClient.create();

		ResponseEntity<String> response = restClient.get().uri(FULL_URL).headers(headers -> {
			headers.set("accept", "application/json, text/plain, */*");
			headers.set("origin", "https://www.investorgain.com");
			headers.set("referer", "https://www.investorgain.com/");
			headers.set("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
		}).retrieve().toEntity(String.class);

		String responseBody = response.getBody();
		JSONObject jsonObject = new JSONObject(responseBody);
		JSONArray ipoListArray = (JSONArray) jsonObject.get("ipoList");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		List<IpoDTO> ipoDTOs = new ArrayList();
		for (Object obj : ipoListArray) {
			JSONObject companyData = (JSONObject) obj;

			String openDtStr = companyData.optString("issue_open_dt", "");
			String closeDtStr = companyData.optString("issue_end_dt", "");

			LocalDate openDate = null;
			LocalDate closeDate = null;

			if (!openDtStr.isEmpty()) {
				openDate = LocalDate.parse(openDtStr, formatter);
			}
			if (!closeDtStr.isEmpty()) {
				closeDate = LocalDate.parse(closeDtStr, formatter);
			}

			LocalDate today = LocalDate.now();
			boolean isOpen = false;
			if (openDate != null && closeDate != null) {
				isOpen = !today.isBefore(openDate) && !today.isAfter(closeDate);
			}

			IpoDTO ipoDTO = new IpoDTO();
			ipoDTO.setCompanyShortName(companyData.getString("company_short_name"));
			ipoDTO.setIssueSize(companyData.getString("issue_size"));
			ipoDTO.setIpoOpenDate(openDate);
			ipoDTO.setIpoClosedDate(closeDate);
			ipoDTO.setIpoCategory(companyData.getString("ipo_category"));
			ipoDTO.setIsOpen(isOpen);
			ipoDTOs.add(ipoDTO);

		}

		// Store or process the data
		return ipoDTOs;
	}

}