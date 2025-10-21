import { Component } from '@angular/core';
import { AnnouncementModel } from '../models/announcement.model';

@Component({
  selector: 'app-announcement',
  imports: [],
  templateUrl: './announcement.html',
  styleUrl: './announcement.css'
})
export class Announcement {
  

  announcements: AnnouncementModel[] = [];

    
}
