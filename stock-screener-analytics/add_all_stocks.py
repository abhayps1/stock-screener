import pandas as pd

# bse_df = pd.read_csv("files/BSE_all_stocks.csv")
# print(bse_df.columns)
# print("Initial number of stocks:", len(bse_df))

# # Check for null values in all columns
# # print("Null values in each column:")
# # print(bse_df.isnull().sum())


# # Filter for rows where Status is 'Active'
# bse_df = bse_df[bse_df['Status'] == 'Active']

# print(f"Number of active stocks: {len(bse_df)}")

# # Save the filtered data to a new CSV file
# bse_df.to_csv("files/BSE_filtered_stocks.csv", index=False)

# # List unique values and their counts for the 'Industry' column
# # print("\nUnique values and their occurrence counts in 'Industry' column:")
# print(bse_df['Industry New Name'].value_counts())

nse_df = pd.read_csv("files/NSE_all_stocks.csv")
print(nse_df.columns)

print("Initial number of stocks:", len(nse_df))
print(nse_df[" SERIES"].value_counts())

# nse_df = nse_df[nse_df['Status'] == 'Active']
nse_df = nse_df[nse_df[' SERIES'].isin(['EQ', 'BE'])]  # Filter for 'EQ' and 'BE' series
# nse_df = nse_df[nse_df[' SERIES'].isin(['BE'])]  # Filter for 'EQ' and 'BE' series

nse_df.to_csv("files/NSE_filtered_stocks.csv", index=False)