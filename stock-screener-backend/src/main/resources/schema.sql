CREATE TABLE stocks (
    symbol VARCHAR(255) NOT NULL UNIQUE,
    company_name VARCHAR(255),
    category VARCHAR(255),
    groww_url VARCHAR(255),
    screener_url VARCHAR(255),
    trendlyne_url VARCHAR(255),
    PRIMARY KEY (symbol)
);