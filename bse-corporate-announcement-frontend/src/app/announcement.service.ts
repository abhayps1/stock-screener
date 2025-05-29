import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Announcement } from './models/announcement.model';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private baseUrl = 'http://localhost:8080/api/announcements';

    constructor(private http: HttpClient) {}

    getLatestAnnouncements(): Observable<Announcement[]> {
        return this.http.get<Announcement[]>(`${this.baseUrl}/latest`);
    }
}
