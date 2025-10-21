import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { StockService } from '../services/stock-service';

@Component({
  selector: 'app-allstocks',
  imports: [CommonModule, DatePipe],
  templateUrl: './allstocks.html',
  styleUrl: './allstocks.css'
})
export class Allstocks implements OnInit {
  stocks = signal<any[]>([]);

  constructor(private stockService: StockService) { }

  ngOnInit(): void {
    this.loadAllStocks();
  }

  loadAllStocks(): void {
    this.stockService.getAllStocksList().subscribe(
      (data) => {
        this.stocks.set(data);
        console.log('All stocks loaded having length:', data.length);
      },
      (error) => {
        console.error('Error fetching all stocks:', error);
      }
    );
  }
}
