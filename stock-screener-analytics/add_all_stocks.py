# This code to is to merge the data of the stocks from BSE and NSE and filter out the active stocks only
# The filtered data is then saved to a new CSV file for further analysis.

import pandas as pd
import numpy as np
import sqlalchemy

bse_df = pd.read_csv("files/BSE_all_stocks.csv")
nse_df = pd.read_csv("files/NSE_all_stocks.csv")
# print(bse_df.columns)
# print(nse_df.columns)

# # Check for null values in all columns for both dataframes
# print("Null values in each column:")
# print(bse_df.isnull().sum())
# print(nse_df.isnull().sum())

# # Remove null value rows from both dataframes if any exist
# bse_df.dropna(inplace=True)
# nse_df.dropna(inplace=True)

# Filter for only active stocks in BSE and for 'EQ' and 'BE' series in NSE
bse_df = bse_df[bse_df['Status'] == 'Active']
nse_df = nse_df[nse_df[' SERIES'].isin(['EQ', 'BE'])]                                                                                                                                                                                                              

# Identify common ISIN numbers in both DataFrames
common_isin = set(bse_df['ISIN No']) & set(nse_df[' ISIN NUMBER'])

# Create a new dataframe called all_stocks_df with columns: Security Code, Symbol, Security Name, ISIN No, Industry Name, Listing Date
selected_columns = ['Security Code', 'Security Id', 'Security Name', 'ISIN No', 'Industry New Name']
all_stocks_df = bse_df[selected_columns].rename(columns={'Security Id': 'Symbol', 'Industry New Name': 'Industry Name'})

# Add another column called Listing Date from nse_df to all_stocks_df based on matching ISIN numbers
all_stocks_df = all_stocks_df.merge(nse_df[[' ISIN NUMBER', ' DATE OF LISTING']], left_on='ISIN No', right_on=' ISIN NUMBER', how='left')
all_stocks_df.drop(' ISIN NUMBER', axis=1, inplace=True)
all_stocks_df.rename(columns={' DATE OF LISTING': 'Listing Date'}, inplace=True)

# Identify ISIN numbers in NSE but not in BSE
unique_nse_isin = set(nse_df[' ISIN NUMBER']) - set(bse_df['ISIN No'])
print(f"ISIN numbers in NSE but not in BSE: {len(unique_nse_isin)}")

# Filter nse_df to only include rows with unique ISINs
filtered_nse_df = nse_df[nse_df[' ISIN NUMBER'].isin(unique_nse_isin)]

# Map columns for filtered_nse_df to match all_stocks_df
mapped_nse_df = filtered_nse_df.rename(columns={
    'SYMBOL': 'Symbol',
    'NAME OF COMPANY': 'Security Name',
    ' DATE OF LISTING': 'Listing Date',
    ' ISIN NUMBER': 'ISIN No'
})[['Symbol', 'Security Name', 'Listing Date', 'ISIN No']]

# Add missing columns with NaN or appropriate default values
mapped_nse_df['Security Code'] = np.nan
mapped_nse_df['Industry Name'] = np.nan

# Reorder columns to match all_stocks_df
mapped_nse_df = mapped_nse_df[['Security Code', 'Symbol', 'Security Name', 'ISIN No', 'Industry Name', 'Listing Date']]

# Concatenate with all_stocks_df
all_stocks_df = pd.concat([all_stocks_df, mapped_nse_df], ignore_index=True)

# Create Endpoint Url column
all_stocks_df['Endpoint Url'] = all_stocks_df['Security Name'].str.lower().str.replace(' ', '-').str.replace('(', '').str.replace(')', '').str.replace('limited', 'ltd').str.replace(',', '').str.replace('&', '').str.replace('__', '-').str.replace('---', '-').str.replace('--', '-')

# Create Screener Url column
all_stocks_df['Screener Url'] = all_stocks_df.apply(
    lambda row: f"https://www.screener.in/company/{int(row['Security Code'])}/consolidated/" if pd.notnull(row['Security Code']) else f"https://www.screener.in/company/{row['Symbol']}/consolidated/",
    axis=1
)

# Create Groww Url column
all_stocks_df['Groww Url'] = 'https://groww.in/stocks/' + all_stocks_df['Endpoint Url']

# Create Trendlyne Url column
all_stocks_df['Trendlyne Url'] = 'https://trendlyne.com/equity/' + all_stocks_df['Symbol'].str.lower() + '/' + all_stocks_df['Endpoint Url']


# Save the updated all_stocks_df to database
engine = sqlalchemy.create_engine('mysql+pymysql://root:root@localhost:3306/stockscreenerdb')
all_stocks_df.to_sql('all_stocks', engine, if_exists='replace', index=False)
