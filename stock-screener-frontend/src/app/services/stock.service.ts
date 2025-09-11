import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock.model';
import { environment } from '../../environments/environment';
import { SearchedStock } from '../models/searched-stock.model';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  
  private stockUrl = environment.backendUrl+ '/api/stock';
  private getAllStocksUrl = this.stockUrl+'/allStocks';
  private addUrl = this.stockUrl+'/add';
  private searchUrl = this.stockUrl+'/search';
  private refreshIndicatorsUrl = this.stockUrl+'/updateIndicatorData';

  constructor(private http: HttpClient) { }

  // getStocks(): Observable<Stock[]> {
  //   return this.http.get<Stock[]>(this.getAllStocksUrl);
  // }

  addStock(stock: SearchedStock): Observable<any> {
    return this.http.post(`${this.stockUrl}/add`, stock);
  }

  // searchStocks(companyTerm: string): Observable<string> {
  //   return this.http.get(`${this.searchUrl}?companyTerm=${encodeURIComponent(companyTerm)}`, { responseType: 'text' });
  // }

  updateIndicatorData() {
    return this.http.get(this.refreshIndicatorsUrl, { responseType: 'text' });
  }

  
}
