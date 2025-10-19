package com.aps.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.aps.dto.SearchedStockDto;
import com.aps.dto.AllStocksDto;

@Repository
public class SearchingRepository {
    private final JdbcTemplate jdbcTemplate;

    public SearchingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SearchedStockDto> searchStock(String term) {
        String sql = """
            SELECT security_code, symbol, name, endpoint 
            FROM all_stocks
            WHERE LOWER(security_code) LIKE LOWER(CONCAT('%', ?, '%'))
               OR LOWER(symbol) LIKE LOWER(CONCAT('%', ?, '%'))
               OR LOWER(name) LIKE LOWER(CONCAT('%', ?, '%'))
        """;

        return jdbcTemplate.query(
            sql,
            new Object[]{term, term, term},
            (rs, rowNum) -> new SearchedStockDto(
                rs.getString("security_code"),
                rs.getString("symbol"),
                rs.getString("name"),
                rs.getString("endpoint")
            )
        );
    }

    public List<SearchedStockDto> searchStockUsingEndpoint(String endpoint) {
        String sql = """
            SELECT security_code, symbol, name, endpoint
            FROM all_stocks
            WHERE endpoint LIKE LOWER(CONCAT('%', ?, '%'))
        """;

        return jdbcTemplate.query(
            sql,
            new Object[]{endpoint},
            (rs, rowNum) -> new SearchedStockDto(
                rs.getString("security_code"),
                rs.getString("symbol"),
                rs.getString("name"),
                rs.getString("endpoint")
            )
        );
    }

    public List<AllStocksDto> getAllStocks() {
        String sql = """
            SELECT security_code, symbol, name, listing_date
            FROM all_stocks
            ORDER BY listing_date DESC
        """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new AllStocksDto(
                rs.getString("security_code"),
                rs.getString("symbol"),
                rs.getString("name"),
                rs.getString("listing_date")
            )
        );
    }

}