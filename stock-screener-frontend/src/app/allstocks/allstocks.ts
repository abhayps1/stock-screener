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
  isUpdating = signal<boolean>(false);
  updateMessage = signal<string>('');

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

  updateAllStocks(): void {
    this.isUpdating.set(true);
    this.updateMessage.set('');
    this.stockService.updateAllStocks().subscribe(
      (response) => {
        this.isUpdating.set(false);
        this.updateMessage.set(response.message || 'All stocks updated successfully');
        this.loadAllStocks(); // Reload the list after update
      },
      (error) => {
        this.isUpdating.set(false);
        this.updateMessage.set('Error updating stocks: ' + (error.error?.error || error.message));
      }
    );
  }
}
