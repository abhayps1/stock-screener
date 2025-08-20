import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock.model';

@Injectable({
  providedIn: 'root'
})
export class StocksService {
  
  private baseUrl = 'http://localhost:8080/api/stock';
  private getAllStocksUrl = this.baseUrl+'/allStocks';
  private addUrl = this.baseUrl+'/add';
  private searchUrl = this.baseUrl+'/search'; // New search endpoint
  private refreshIndicatorsUrl = this.baseUrl+'/updateIndicatorData'; // New endpoint for refreshing indicators

  constructor(private http: HttpClient) { }

  getStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(this.getAllStocksUrl);
  }

  // Add via reference URL: POST /add?referenceUrl=...
  addStock(referenceUrl: string): Observable<any> {
    return this.http.post(this.addUrl, null, { params: { referenceUrl } });
  }

  searchStocks(companyTerm: string): Observable<string> {
    return this.http.get(`${this.searchUrl}?companyTerm=${encodeURIComponent(companyTerm)}`, { responseType: 'text' });
  }

  updateIndicatorData() {
    return this.http.get(this.refreshIndicatorsUrl, { responseType: 'text' });
  }
}
