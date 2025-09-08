import { Component } from '@angular/core';
import { Announcement } from '../models/announcement.model';
import { AnnouncementService } from '../services/announcement.service';

@Component({
  selector: 'app-announcements',
  templateUrl: './announcements.component.html',
  styleUrls: ['./announcements.component.css']
})
export class AnnouncementsComponent {

  announcements: Announcement[] = [];

    constructor(private announcementService: AnnouncementService) {}

    ngOnInit(): void {
      console.log("hell it worked");
        this.announcementService.getLatestAnnouncements().subscribe(data => {
            this.announcements = data;
        });
    }
}