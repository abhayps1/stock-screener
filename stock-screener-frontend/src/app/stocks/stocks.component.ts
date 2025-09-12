import { Component, OnInit, OnDestroy } from '@angular/core';
import { StockService } from '../services/stock.service';
import { Stock } from '../models/stock.model';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-stocks',
  templateUrl: './stocks.component.html'
})
export class StocksComponent implements OnInit, OnDestroy {
  stocks: Stock[] = [];
  newStock: Stock = {stockName: '', growwUrl: '', screenerUrl: '', trendlyneUrl: '' };
  companyTerm: string = ''; // New search variable
  searchResponseHTML: string = '';
  showForm = false;
  hoveredStock: Stock | null = null;
  private destroy$ = new Subject<void>();
  private inputSubject = new Subject<string>();

  constructor(private stockService: StockService) {}

  ngOnInit(): void {
    // this.loadStocks();
    // this.setupDebouncedInput();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onStockSelected(stock: any): void {
    this.addStock(stock);
  }

  addStock(stock: any): void {
    this.stockService.addStock(stock).subscribe(
      response => {
        console.log('Stock added successfully:', response);
        // Optionally, refresh stocks list or update UI here
      },
      error => {
        console.error('Error adding stock:', error);
        // Optionally, show an error message
      }
    );
  }
}
