import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock.model';
import { environment } from '../../environments/environment';
import { SearchedStock } from '../models/searched-stock.model';
import { Result } from '../models/result.model';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  
  private stockUrl = environment.backendUrl+ '/api/stock';
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
}
