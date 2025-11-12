from datetime import date
import requests
import pandas as pd
import numpy as np
from io import StringIO
import sqlalchemy
from logger_util import get_console_logger
from bs4 import BeautifulSoup
import html
import re
import json
import os

logger = get_console_logger("app_util")

def getDeliveryVolume(response_text, stock_num):
    json_data = json.loads(response_text)
    volume_analysis = json_data["body"]['parameters']['volume_analysis']
    day_volume = volume_analysis["tableData"][0]
    current_date = date.today()
    daily_total_volume = day_volume[1]
    daily_delivery_percent = day_volume[2]
    daily_delivery_volume = day_volume[3]
    daily_delivery_data = {"dates" : current_date,
        "daily_total_vol": [daily_total_volume],
        "daily_delivery_prcnt": [daily_delivery_percent],
        "daily_delivery_vol": [daily_delivery_volume]
    }

    daily_delivery_data_df = pd.DataFrame(daily_delivery_data)
    if not os.path.exists(f"files/{stock_num}.csv"):
        daily_delivery_data_df.to_csv(f"files/{stock_num}.csv", index=False)
        logger.info("Stock Delivery Volume file created.")
    else:
        df = pd.read_csv(f"files/{stock_num}.csv")
        if(str(current_date) not in df['dates'].values):
            logger.info("Logic not working")
            df = pd.concat([daily_delivery_data_df, df], ignore_index=True)
            df.to_csv(f"files/{stock_num}.csv", index=False)
            logger.info("Stock Delivery Volume file updated with new date entry.")
        else:
            logger.info("Date entry already exists. No update made.")

def get_financial_data_for_stock(trendlyne_id):
    url = f"https://trendlyne.com/equity/api/stock/adv-technical-analysis/{trendlyne_id}/24/"
    payload = {}
    headers = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36'
    }

    response = requests.request("GET", url, headers=headers, data=payload)
    
    if(200 == response.status_code):
        logger.info("Technical api response code : "+str(response.status_code))
        response_body = response.text
        content_type = response.headers['Content-Type']
        logger.info("Technical api Response content type : "+content_type)
        if(None != response_body and "" != response_body and "application/json" == content_type):
            logger.info("Technical api response is ready for preprocessing")
            getDeliveryVolume(response_body, trendlyne_id)
            return "Hello"
        logger.error("The response of Technical api is None/Empty or not in JSON format")
        return ""
    logger.error("Technical api Request failed with response code : "+str(response.status_code))
    return ""

def fetch_bse_data():
    # This code can be hard-replaced as per change in BSE website.
    url = "https://api.bseindia.com/BseIndiaAPI/api/ListofScripData/w?Group=&Scripcode=&industry=&segment=Equity&status=Active"
    payload = {}
    headers = {
    'referer': 'https://www.bseindia.com/',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36'
    }
    response = requests.request("GET", url, headers=headers, data=payload)

    logger.info("BSE API Response Status Code : "+str(response.status_code))
    if(200 == response.status_code):
        response_body = response.text
        content_type = response.headers['Content-Type']
        logger.info("BSE API Response Content Type : "+content_type)
        if(None != response_body and "" != response_body and "application/json; charset=utf-8" == content_type):
            logger.info("The response of BSE API is parsable and is ready to be processed as csv")
            return response_body
       
        logger.error("The response of BSE API is None/Empty or not in JSON format")
        return ""
    logger.error("BSE API Request failed with response code : "+str(response.status_code))
    return ""

def fetch_nse_data():

    # This code can be hard-replaced as per change in NSE website.
    url = "https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv"

    payload = {}
    headers = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36'
    }
    response = requests.request("GET", url, headers=headers, data=payload)

    # logger.info("response text : ", response.text)
    logger.info("NSE API Response Status Code : "+str(response.status_code))
    if(200 == response.status_code):
        response_body = response.text
        content_type = response.headers['Content-Type']
        logger.info("NSE API Response Content Type : "+content_type)
        if(None != response_body and "" != response_body and "text/csv" == content_type):
            logger.info("The response of NSE API is parsable and is ready to be processed as csv")
            return response_body
       
        logger.error("The response of NSE API is None/Empty or not in JSON format")
        return ""
    logger.error("NSE API Request failed with response code : "+str(response.status_code))
    return ""

def extract_company_slug(url):
    # Split the URL by '/' and get the parts
    parts = url.rstrip('/').split('/')
    # The slug is typically the 5th part (index 4) in the URL structure
    if len(parts) >= 5:
        return parts[4]
    return None

def update_all_stocks_data():

    bse_response = fetch_bse_data()
    nse_response = fetch_nse_data()
    
    if("" == bse_response or "" == nse_response):
        logger.info("One of the API response is empty. Hence exiting without processing further.")
        return None

    try:
        bse_df = pd.read_json(StringIO(bse_response))
        logger.info(f"BSE data parsed, \nTotal records fetched: {len(bse_df)}")
    except Exception as e:
        logger.error("Error parsing BSE CSV:", e)
        # logger.error("Raw content of BSE API:", bse_response.content.decode('utf-8', errors='replace'))

    try:
        nse_df = pd.read_csv(StringIO(nse_response))
        logger.info(f"NSE data parsed, \nTotal records fetched: {len(nse_df)}")
    except Exception as e:
        logger.error(f"Error parsing NSE CSV: {e}")
        # logger.error("Raw content of NSE API:\n" + str(nse_response))

    # print(bse_df.columns)
    # print(nse_df.columns)

    # Filter for only active stocks in BSE and for 'EQ' and 'BE' series in NSE
    bse_df = bse_df[bse_df['Status'] == 'Active']
    nse_df = nse_df[nse_df[' SERIES'].isin(['EQ', 'BE'])]

    print("The size of the nse dataframe is : "+str(len(nse_df)))
    print("The size of the bse dataframe is : "+str(len(bse_df)))

    # Identify common ISIN numbers in both DataFrames
    common_isin = set(bse_df['ISIN_NUMBER']) & set(nse_df[' ISIN NUMBER'])

    # Create the column endpoint in bse_df based on bse_url
    bse_df['endpoint'] = bse_df['NSURL'].apply(extract_company_slug)

    # Create a new dataframe called all_stocks_df with columns: Security Code, Symbol, Security Name, ISIN No, Industry Name, Listing Date
    selected_columns = ['SCRIP_CD', 'scrip_id', 'Scrip_Name', 'ISIN_NUMBER', 'INDUSTRY', 'GROUP', 'endpoint']
    all_stocks_df = bse_df[selected_columns].rename(columns={'SCRIP_CD' : 'security_code', 'scrip_id': 'symbol', 'Scrip_Name' : 'name','ISIN_NUMBER' : 'isin',  'INDUSTRY': 'industry', 'GROUP': 'group'})

    # Add another column called Listing Date from nse_df to all_stocks_df based on matching ISIN numbers
    all_stocks_df = all_stocks_df.merge(nse_df[[' ISIN NUMBER', ' DATE OF LISTING']], left_on='isin', right_on=' ISIN NUMBER', how='left')
    all_stocks_df.drop(' ISIN NUMBER', axis=1, inplace=True)
    all_stocks_df.rename(columns={' DATE OF LISTING': 'listing_date'}, inplace=True)

    # Identify ISIN numbers in NSE but not in BSE
    unique_nse_isin = set(nse_df[' ISIN NUMBER']) - set(bse_df['ISIN_NUMBER'])
    
    # Filter nse_df to only include rows with unique ISINs
    filtered_nse_df = nse_df[nse_df[' ISIN NUMBER'].isin(unique_nse_isin)].copy()
    filtered_nse_df.loc[:, 'endpoint'] = filtered_nse_df['NAME OF COMPANY'].str.lower().str.replace(' ', '-').str.replace('(', '').str.replace(')', '').str.replace('limited', 'ltd').str.replace(',', '').str.replace('&', '').str.replace('__', '-').str.replace('---', '-').str.replace('--', '-').str.replace('-$', '')

    # Map columns for filtered_nse_df to match all_stocks_df
    mapped_nse_df = filtered_nse_df.rename(columns={
        'SYMBOL': 'symbol',
        'NAME OF COMPANY': 'name',
        ' DATE OF LISTING': 'listing_date',
        ' ISIN NUMBER': 'isin'
    })[['symbol', 'name', 'listing_date', 'isin', 'endpoint']]

    
    # Add missing columns with NaN or appropriate default values
    mapped_nse_df['security_code'] = np.nan
    mapped_nse_df['industry'] = np.nan
    mapped_nse_df['group'] = np.nan

    # Reorder columns to match all_stocks_df
    mapped_nse_df = mapped_nse_df[['security_code', 'symbol', 'name', 'isin', 'industry', 'group', 'endpoint',
        'listing_date']]

    # Concatenate with all_stocks_df
    all_stocks_df = pd.concat([all_stocks_df, mapped_nse_df], ignore_index=True)

    # Convert security_code to string without .0
    all_stocks_df['security_code'] = all_stocks_df['security_code'].astype(str).str.replace('.0', '', regex=False)

    # Convert listing_date to date format (DD-MMM-YYYY) without time
    all_stocks_df['listing_date'] = pd.to_datetime(all_stocks_df['listing_date'], format='%d-%b-%Y', errors='coerce').dt.date

    # Add 'is_nse' column: True if stock belongs to nse_df, False otherwise
    all_stocks_df['is_nse'] = all_stocks_df['isin'].isin(set(nse_df[' ISIN NUMBER']))

    # Save the updated all_stocks_df to database
    engine = sqlalchemy.create_engine('mysql+pymysql://root:root@localhost:3306/stockscreenerdb')
    all_stocks_df.to_sql('all_stocks', engine, if_exists='replace', index=False)
    logger.info("All stocks data updated and saved to database successfully.")

