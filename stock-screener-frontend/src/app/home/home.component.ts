import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StockService } from '../services/stock.service';
import { Link } from '../models/link.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  url: string = '';
  links: Link[] = [];

  constructor(private stockService: StockService) {}

  ngOnInit(): void {
    this.loadLinks();
  }

  saveLink(): void {
    if (this.url.trim()) {
      this.stockService.saveLink(this.url).subscribe({
        next: (response) => {
          console.log('Link saved:', response);
          this.url = '';
          this.loadLinks();
        },
        error: (error) => {
          console.error('Error saving link:', error);
        }
      });
    }
  }

  onEnterKey(event: any): void {
    if (event.key === 'Enter') {
      this.saveLink();
    }
  }

  deleteLink(id: number): void {
    this.stockService.deleteLink(id).subscribe({
      next: (response) => {
        console.log('Link deleted:', response);
        this.loadLinks();
      },
      error: (error) => {
        console.error('Error deleting link:', error);
      }
    });
  }

  loadLinks(): void {
    this.stockService.getAllLinks().subscribe({
      next: (data) => {
        this.links = data;
      },
      error: (error) => {
        console.error('Error loading links:', error);
      }
    });
  }
}
