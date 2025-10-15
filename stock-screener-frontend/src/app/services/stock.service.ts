import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock.model';
import { environment } from '../../environments/environment';
import { SearchedStock } from '../models/searched-stock.model';
import { Result } from '../models/result.model';
import { Watchlist } from '../models/watchlist.model';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  
  
  private stockUrl = environment.backendUrl+ '/api/stock';
  private watchlistUrl = environment.backendUrl + '/api/watchlist';
  private getAllStocksUrl = this.stockUrl+'/allStocks';
  private addUrl = this.stockUrl+'/add';
  private searchUrl = this.stockUrl+'/search';
  private refreshIndicatorsUrl = this.stockUrl+'/updateIndicatorData';
  private getResultsUrl = this.stockUrl + '/getResults';

  constructor(private http: HttpClient) { }

  getStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(this.getAllStocksUrl);
  }

  addStock(stock: SearchedStock): Observable<any> {
    return this.http.post(`${this.stockUrl}/add`, stock, { responseType: 'text' });
  }

  updateIndicatorData() {
    return this.http.get(this.refreshIndicatorsUrl, { responseType: 'text' });
  }

  getAllResults(): Observable<Result[]> {
    return this.http.get<Result[]>(this.getResultsUrl);
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
}
