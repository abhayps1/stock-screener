import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LinkModel } from '../models/link.model';
import { StockService } from '../services/stock-service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  url = signal('');
  links = signal<LinkModel[]>([]);

  constructor(private stockService: StockService) {}

  ngOnInit(): void {
    console.log("Load Links called");
    this.loadLinks();
  }

  saveLink(): void {
    if (this.url().trim()) {
      this.stockService.saveLink(this.url()).subscribe({
        next: (response) => {
          console.log('Link saved:', response);
          this.url.set('');
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
    console.log("Inside loadLinks");
    this.stockService.getAllLinks().subscribe({
      next: (data) => {
        this.links.set(data);
        console.log('Links loaded: Data is put inside the links entity');
      },
      error: (error) => {
        console.error('Error loading links:', error);
      }
    });
  }
}
