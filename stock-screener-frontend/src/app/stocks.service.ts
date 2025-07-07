import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from './models/stock.model';

@Injectable({
  providedIn: 'root'
})
export class StocksService {
  private apiUrl = 'http://localhost:8080/api/stock/allStocks';
  private addUrl = 'http://localhost:8080/api/stock/add';

  constructor(private http: HttpClient) { }

  getStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(this.apiUrl);
  }

  addStock(stock: Stock): Observable<any> {
    return this.http.post(this.addUrl, stock);
  }
}
