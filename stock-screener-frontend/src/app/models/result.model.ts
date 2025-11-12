export interface ResultModel {
  stockName: string;
  resultDate: string; // ISO date string
  latestQuarter: string;
  marketCap: number;
  peRatio: number;
  industryPe: number;
  quarterlyRevenueCngPrcnt: number;
  quarterlyProfitCngPrcnt: number;
  yearlyRevenueCngPrcnt: number;
  yearlyProfitCngPrcnt: number;
  yearlyNetworthCngPrcnt: number;
  growwUrl: string;
  screenerUrl: string;
  trendlyneUrl: string;
  perplexityUrl: string;
}
