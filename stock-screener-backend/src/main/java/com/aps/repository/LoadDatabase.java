//package com.bsecab.repository;
//
//import java.math.BigDecimal;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.bsecab.entity.Stock;
//
//@Configuration
//public class LoadDatabase {
//
//	@Bean
//	CommandLineRunner initDatabase(StockRepository repository) {
//		return args -> {
//			Stock stock = new Stock();
//			stock.setSymbol("INFY");
//			stock.setName("Infosys Ltd.");
//			stock.setCurrentPrice(BigDecimal.valueOf(1425.50));
//			stock.setWeek52High(BigDecimal.valueOf(1600.00));
//			stock.setWeek52Low(BigDecimal.valueOf(1200.00));
//			stock.setMarketCap(BigDecimal.valueOf(600000.00));
//			stock.setPeRatio(25.4);
////			stock.setVolume(BigDecimal.valueOf(2500000));
////			stock.setLastUpdated(LocalDateTime.now());
//
//			repository.save(stock);
//		};
//	}
//}
