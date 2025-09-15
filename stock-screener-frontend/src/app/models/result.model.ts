export interface Result {
  stockName: string;
  resultDate: string; // ISO date string
  latestQuarter: string;
  quarterlyRevenueCngPrcnt: number;
  quarterlyProfitCngPrcnt: number;
  yearlyRevenueCngPrcnt: number;
  yearlyProfitCngPrcnt: number;
  yearlyNetworthCngPrcnt: number;
  growwUrl: string;
  screenerUrl: string;
  trendlyneUrl: string; 
}
