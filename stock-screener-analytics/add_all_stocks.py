# This code to is to merge the data of the stocks from BSE and NSE and filter out the active stocks only
# The filtered data is then saved to a new CSV file for further analysis.

import pandas as pd
import numpy as np
import sqlalchemy


import requests
import pandas as pd
import io

def update_all_stocks_data():

    bse_url = "https://api.bseindia.com/BseIndiaAPI/api/LitsOfScripCSVDownload/w?segment=Equity&status=&industry=&Group=&Scripcode="
    bse_headers = {
        "Referer": "https://www.bseindia.com/",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
    }

    bse_response = requests.get(bse_url, headers=bse_headers)
    bse_response.raise_for_status()
    print("BSE stocks data downloaded successfully.")

    nse_url = "https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv"
    nse_headers = {
        "Referer": "https://www.nseindia.com/",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
    }

    nse_response = requests.get(nse_url, headers=nse_headers)
    nse_response.raise_for_status()
    print("NSE stocks data downloaded successfully.")

    bse_df = pd.read_csv(io.BytesIO(bse_response.content))
    nse_df = pd.read_csv(io.BytesIO(nse_response.content))

    # Filter for only active stocks in BSE and for 'EQ' and 'BE' series in NSE
    bse_df = bse_df[bse_df['Status'] == 'Active']
    nse_df = nse_df[nse_df[' SERIES'].isin(['EQ', 'BE'])]

    # Identify common ISIN numbers in both DataFrames
    common_isin = set(bse_df['ISIN No']) & set(nse_df[' ISIN NUMBER'])

    # Create a new dataframe called all_stocks_df with columns: Security Code, Symbol, Security Name, ISIN No, Industry Name, Listing Date
    selected_columns = ['Security Code', 'Security Id', 'Security Name', 'ISIN No', 'Industry New Name', 'Group']
    all_stocks_df = bse_df[selected_columns].rename(columns={'Security Code' : 'security_code', 'Security Id': 'symbol', 'Security Name' : 'name','ISIN No' : 'isin',  'Industry New Name': 'industry', 'Group': 'group'})

    # Add another column called Listing Date from nse_df to all_stocks_df based on matching ISIN numbers
    all_stocks_df = all_stocks_df.merge(nse_df[[' ISIN NUMBER', ' DATE OF LISTING']], left_on='isin', right_on=' ISIN NUMBER', how='left')
    all_stocks_df.drop(' ISIN NUMBER', axis=1, inplace=True)
    all_stocks_df.rename(columns={' DATE OF LISTING': 'listing_date'}, inplace=True)

    # Identify ISIN numbers in NSE but not in BSE
    unique_nse_isin = set(nse_df[' ISIN NUMBER']) - set(bse_df['ISIN No'])

    # Filter nse_df to only include rows with unique ISINs
    filtered_nse_df = nse_df[nse_df[' ISIN NUMBER'].isin(unique_nse_isin)]

    # Map columns for filtered_nse_df to match all_stocks_df
    mapped_nse_df = filtered_nse_df.rename(columns={
        'SYMBOL': 'symbol',
        'NAME OF COMPANY': 'name',
        ' DATE OF LISTING': 'listing_date',
        ' ISIN NUMBER': 'isin'
    })[['symbol', 'name', 'listing_date', 'isin']]

    # Add missing columns with NaN or appropriate default values
    mapped_nse_df['security_code'] = np.nan
    mapped_nse_df['industry'] = np.nan
    mapped_nse_df['group'] = np.nan

    # Reorder columns to match all_stocks_df
    mapped_nse_df = mapped_nse_df[['security_code', 'symbol', 'name', 'isin', 'industry', 'group',
        'listing_date']]

    # Concatenate with all_stocks_df
    all_stocks_df = pd.concat([all_stocks_df, mapped_nse_df], ignore_index=True)

    # Create Endpoint column
    all_stocks_df['endpoint'] = all_stocks_df['name'].str.lower().str.replace(' ', '-').str.replace('(', '').str.replace(')', '').str.replace('limited', 'ltd').str.replace(',', '').str.replace('&', '').str.replace('__', '-').str.replace('---', '-').str.replace('--', '-').str.replace('-$', '')

    # Convert security_code to string without .0
    all_stocks_df['security_code'] = all_stocks_df['security_code'].astype(str).str.replace('.0', '', regex=False)

    # Save the updated all_stocks_df to database
    engine = sqlalchemy.create_engine('mysql+pymysql://root:root@localhost:3306/stockscreenerdb')
    all_stocks_df.to_sql('all_stocks', engine, if_exists='replace', index=False)
    print("All stocks data updated and saved to database successfully.")

