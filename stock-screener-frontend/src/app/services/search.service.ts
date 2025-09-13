import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SearchedStock } from '../models/searched-stock.model';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private backendUrl = environment.backendUrl+ '/api/stock';
  private searchUrl = this.backendUrl+'/search';

  constructor(private http: HttpClient) { }

  searchStocks(companyTerm: string): Observable<SearchedStock[]> {
      console.log("Searching for term:", companyTerm);
      return this.http.get<SearchedStock[]>(`${this.searchUrl}?companyTerm=${encodeURIComponent(companyTerm)}`);
  }

}
