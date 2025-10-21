import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Stock } from '../models/stock.model';
import { Observable } from 'rxjs';
import { SearchedStockModel } from '../models/searched-stock.model';
import { ResultModel } from '../models/result.model';
import { LinkModel } from '../models/link.model';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  private stockUrl = environment.backendUrl+ '/api/stock';
  private watchlistUrl = environment.backendUrl + '/api/watchlist';
  private saveLinkUrl = environment.backendUrl + '/api/link/saveLink';
  private getLinksUrl = environment.backendUrl + '/api/link/getLinks';
  private getAllStocksUrl = this.stockUrl+'/allStocks';
  private addUrl = this.stockUrl+'/add';
  private searchUrl = this.stockUrl+'/search';
  private refreshIndicatorsUrl = this.stockUrl+'/updateIndicatorData';
  private getResultsUrl = this.stockUrl + '/getResults';

  constructor(private http: HttpClient) { }

  getStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(this.getAllStocksUrl);
  }

  addStock(stock: SearchedStockModel): Observable<any> {
    return this.http.post(`${this.stockUrl}/add`, stock, { responseType: 'text' });
  }

  updateIndicatorData() {
    return this.http.get(this.refreshIndicatorsUrl, { responseType: 'text' });
  }

  getAllResults(): Observable<ResultModel[]> {
    return this.http.get<ResultModel[]>(this.getResultsUrl);
  }

  fetchResults(): Observable<string> {
    return this.http.post(`${this.stockUrl}/fetchResults`, null, { responseType: 'text' });
  }

  getAllWatchlistNames(): Observable<String[]> {
    return this.http.get<String[]>(`${this.watchlistUrl}/names`);
  }

  createWatchlist(newWatchlistName: String) {
    return this.http.post(`${this.watchlistUrl}/create?newWatchlistName=${newWatchlistName}`, null, { responseType: 'text' });
  }

  getAllWatchlists(): Observable<String[]> {
    return this.http.get<String[]>(this.watchlistUrl);
  }

  saveLink(url: string): Observable<any> {
    return this.http.post(this.saveLinkUrl, null, { params: { url }, responseType: 'text' });
  }

  deleteLink(id: number): Observable<any> {
    return this.http.delete(`${this.getLinksUrl.replace('/getLinks', '')}/deleteLink/${id}`, { responseType: 'text' });
  }

  getAllLinks(): Observable<LinkModel[]> {
    console.log("Inside getAllLinks of StockService");
    return this.http.get<LinkModel[]>(this.getLinksUrl);
  }

  getAllStocksList(): Observable<any[]> {
    return this.http.get<any[]>(this.stockUrl + '/allStocksList');
  }

  searchStocks(companyTerm: string): Observable<SearchedStockModel[]> {
      console.log("Searching for term:", companyTerm);
      return this.http.get<SearchedStockModel[]>(`${this.searchUrl}?companyTerm=${encodeURIComponent(companyTerm)}`);
  }

  getResultQuarter(): string {
    const currentDate = new Date();
    const month = currentDate.getMonth() + 1;
    const year= currentDate.getFullYear() % 100;
    if (month >= 4 && month <= 6) return 'Mar \''+year;     // Current Q1 → previous ends in Mar
    else if (month >= 7 && month <= 9) return 'Jun  \''+year; // Current Q2 → previous ends in Jun
    else if (month >= 10 && month <= 12) return 'Sep \''+year; // Current Q3 → previous ends in Sep
    else return 'Dec \''+(year-1); // Current Q4 → previous ends in Dec
  }
  
}
