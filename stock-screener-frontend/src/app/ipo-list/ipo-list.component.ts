import { Component, OnInit } from '@angular/core';
import { UpcomingIpoService } from '../services/ipo.service';
import { IpoDTO } from '../models/ipo-dto.model'; // ✅ Updated model import

@Component({
  selector: 'app-ipo-list',
  templateUrl: './ipo-list.component.html',
  styleUrls: ['./ipo-list.component.css']
})
export class IpoListComponent implements OnInit {

  ipos: IpoDTO[] = [];
  showForm = false;

  newIpo: IpoDTO = {
    companyShortName: '',
    issueSize: '',
    ipoOpenDate: null,
    ipoClosedDate: null,
    ipoCategory: '',
    isOpen: false
  };

  constructor(private ipoService: UpcomingIpoService) {}

  ngOnInit(): void {
    this.fetchIpos();
  }

  fetchIpos() {
    this.ipoService.getAllIpos().subscribe(data => this.ipos = data);
  }

  
}
