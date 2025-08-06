import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from './models/stock.model';

@Injectable({
  providedIn: 'root'
})
export class StocksService {
  private baseUrl = 'http://localhost:8080/api/stock';
  private getAllStocksUrl = this.baseUrl+'/allStocks';
  private addUrl = this.baseUrl+'/add';
  private searchUrl = this.baseUrl+'/search'; // New search endpoint

  constructor(private http: HttpClient) { }

  getStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(this.getAllStocksUrl);
  }

  addStock(payload: { symbol: string; category?: string }): Observable<any> {
    return this.http.post(this.addUrl, payload);
  }

  searchStocks(companyTerm: string): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.searchUrl}?companyTerm=${encodeURIComponent(companyTerm)}`);
  }
}
