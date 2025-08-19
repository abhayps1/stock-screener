import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable } from 'rxjs';
import { IpoDTO } from '../models/ipo-dto.model'; // adjust path if needed

@Injectable({
  providedIn: 'root'
})
export class UpcomingIpoService {

  private apiUrl = 'http://localhost:8080/api/ipos'; // Your Spring Boot API endpoint

  constructor(private http: HttpClient) { }

  getAllIpos(): Observable<IpoDTO[]> {
    return this.http.get<IpoDTO[]>(this.apiUrl).pipe(
      map(ipos => ipos.map(ipo => ({
        ...ipo,
        issueSize: ipo.issueSize?.replace(/&#8377;/g, '₹ ') || 'N/A'
      })))
    );
  }

  
}
