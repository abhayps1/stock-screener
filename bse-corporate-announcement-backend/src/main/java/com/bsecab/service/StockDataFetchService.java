package com.bsecab.service;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsecab.repository.StockRepository;

@Service
public class StockDataFetchService {

	private static final Logger logger = LoggerFactory.getLogger(StockDataFetchService.class);

	@Autowired
	private StockRepository repository;

	public String fetchStockDataUsingSymbol(String symbol) {
		String url = "https://www.nseindia.com/api/quote-equity?symbol=" + symbol;

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
		headers.set("Accept-Language", "en-US,en;q=0.9,hi;q=0.8");
		headers.set("Referer", "https://www.nseindia.com/get-quotes/equity?symbol=" + symbol);
		headers.set("Sec-Fetch-Site", "same-origin");
		headers.set("Sec-Fetch-Mode", "cors");
		headers.set("Sec-Fetch-Dest", "empty");
		headers.set("Sec-Ch-Ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"");
		headers.set("Sec-Ch-Ua-Mobile", "?0");
		headers.set("Sec-Ch-Ua-Platform", "\"Windows\"");

		// NSE blocks direct API calls without cookies, set a user session cookie from
		// browser

		headers.set("Cookie",
				"_ga=GA1.1.1110393753.1750236323; nseQuoteSymbols=[{\"symbol\":\"INFY\",\"identifier\":\"\",\"type\":\"equity\"}]; AKA_A2=A; bm_mi=843ACD6B5845F127DADF1852D9A3BC9E~YAAQDLcsMZEzQnyXAQAArTILoBwqwnEGMvJX9wLA7IB3Pqcb6zZ6ou1OEIxJhw1FrBe34nO5YATB/FnBZ9ICYKm0BbdHzg+hdVMgvtC0hT5fA81wwROkqkNDhEfDOv5R5d0Z8i98Yg+Mx17PHCnPOlY6E+dXkYcPxrJfW3loIQUPfJdTlqcHrqxBVgiO4rgPhzzyhIWoPuxUj4M2rUVMs+NbC8y2J9UmV+9r3Yr2pJMTfCJ7xO6vBLD1vQh5pZ/lbF8JBjxJpt9lDsqaWuWECns7A8vHfPVeZ/MyohFt67m9kHTRvDnxfYvZbfpLtWE=~1; ak_bmsc=44B9B46F081FB8FBC3902E6FCAC63960~000000000000000000000000000000~YAAQDLcsMdwzQnyXAQAATTgLoBzt24Hwb2zBA00xxveIh4XUgxV6cW/DTafDHLJn64+nznlbAi3XTufxsIOA+TEjXwFoaTOhevZxN4FioYD/TrE82vYyf7zBpDdiS32br0I/muQoW9CmFJB4jhJbRb4lw8rDpRB97SQfeuum94iDyKkMgmtL6/+SHfA2kRcGGMkieRH7j2X8EkV+vqkD/AcjgMz/HTQeiRulpLrIfMFhhiHohRWfwDYr6mqj9xwXF5NngqwK2BPKWWPycIu+SnWLCcDDJ1HKwN/J6JoTivhzuMQPntjpS7nhIHPY+VsVTjQuryIILqv406+eTvLKLrEz/wMQudkEvHuXnhaWmr0t4Qll7dECggfhjIwdAaid+QGMlx2SsfOVfzMIJzbpQ/t5wB2CytTR0pUMlWq2TSqd8z9YfF6Wz7bqCc7tOBWbg59bKho9w4geVrbnA0vaWOt8s0IyrK04jExWy4jy+c47EfjjNTM=; nsit=NsYeDMnwk0vq8x_Abv0rDT78; nseappid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhcGkubnNlIiwiYXVkIjoiYXBpLm5zZSIsImlhdCI6MTc1MDczNjc4NywiZXhwIjoxNzUwNzQzOTg3fQ.03ukKNERSbXBJvSvnNa8pxRcjzobm088UY0G07OmjOY; bm_sz=16EC86ECB8A87BF75B0168A7F6406F35~YAAQDLcsMZA1QnyXAQAAh1oLoByqQ6BDwALarDMn8j1GqxOs5SoA2GWrsJX9jUztQVOa17jb8p5W8dJBT8czQiOYrkt1mM+DJPZV+Ik7TraJD1QJ/quBzIJ+PRyaIFF42yVBHsV/Idz+/mj1EAxlQnr6gyeREDCFTzHrwWVYSZNvO7hkWQ3ftHWl0DbnJg1reJtHXfaWudxkHBwsLppaTmUCSvlbw1StL3WrpOgwaJr55x8oL8b759uyZw7d0HNgmavTXKmOQRtT6Z4cXCxGftnUica6UJ2sHJv0gDMMLx0ENlHNRedmGCVBcRTU7i1bV2tduYeofdRpihf1n9CSqU7AEd7vkafr/QjQ9XDgrpizOWPsuQgp6yLqAMhzPRmCJJ5pca/l6j+cdidWu59jKSE8rz5lA2DMc50QeTvo4Jc8OAzI757H~4273716~3354691; _ga_87M7PJ3R97=GS2.1.s1750736780^$o4^$g1^$t1750736789^$j51^$l0^$h0; _abck=692BBD37E764C2EF466FF6C0C0AD289C~0~YAAQDLcsMTM2QnyXAQAA2mILoA4xL9ALofrqYXGkJRqH5IUKQbFCoL3jO/3FShL+gOz5j3mxvpBXnNeMGKjYY5KJcTjfcHPtPB7uHDjNMT6pzgLgZK/nMKgX8oG4OoMj5b7y2AK4GclAJsVTshFDVm98L0Kt0wv/98JgJkxixyyVF3DrU5bAQEwU+AW+XNyLzpPxEwFhbdSzAODaxfKijo+EwzKucG6RJYnDC08MYx5LVLbzgCZB3g31VEVPrg8OUJ9l/VyiIAITwi/k4ZhSe9T+52U2jKTTZCKRZPJXwiqJh+lfKnJqIhDrVx7QGFeTlCv5F86lNe6OEhpS9/p3NYqZkGclvUeeqCyl34gIfZqSEpbts9Uhcl1Eh0TYGqWJ3ckiOJdrwTek52HKZ3REFL6MP4FBjl3L9KX1v8DqBSYbjulohh87l/dg8jIFzktcQGeuoENK0pxanOfZHX8QRAK7zJZRsCKnBlpQ36FWgw9IlC8ub78B3kmddngehQ2OVStO18ltau8lHVPgbq4elwcZmChIJKzbcAbHj0mRqFUndft6DSVVPV2qe2GOk52al/cqYxUfM+od/c3tAqjpyOVXbE6Q~-1~-1~-1; bm_sv=61DABDABAFE7DD2A05A0585037BDBBEE~YAAQDLcsMTQ2QnyXAQAA2mILoBwxGkWfQfquOkLkmjnmjyFJLzZKKpg7joZm/in3krXCyZmb/AyZES6u0S+v6zSManS2P96P6aMKPqKmKOlM0Lz8amNmDRlO2w6nTXsxrOv1QT40MuaTdrNXdf4UUoruZP8F6lD+KGLuFqr+qYm4S7XzVk8iDvXWeBfodaCCYh6vnu2ioVgGnRnC6wHiMVbA2CBr/o0F+TRSJauYjxX2H6oKq7De6XdKtXxsRD4686xU~1");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Replace with working cookie if required

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			String body = response.getBody();
			return response.getBody();

		} catch (Exception e) {
			System.out.println("Check");
			e.printStackTrace();
			return "Failed to fetch stock data: " + e.getMessage();
		}
	}
}
