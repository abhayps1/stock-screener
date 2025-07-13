import { Component, OnInit } from '@angular/core';
import { StocksService } from '../stocks.service';
import { Stock } from '../models/stock.model';

@Component({
  selector: 'app-stocks',
  templateUrl: './stocks.component.html'
})
export class StocksComponent implements OnInit {
  groupedStocks: { [category: string]: Stock[] } = {};
  newStock: Stock = {symbol : '', companyName: '', category: '', growwUrl: '', screenerUrl: '', trendlyneUrl: '' };
  showForm = false;
  hoveredStock: Stock | null = null;

  constructor(private stocksService: StocksService) {}

  ngOnInit(): void {
    this.loadStocks();
  }

  loadStocks() {
    this.stocksService.getStocks().subscribe(stocks => {
      this.groupedStocks = stocks.reduce((acc, stock) => {
        const category = stock.category || 'Uncategorized';
        if (!acc[category]) {
          acc[category] = [];
        }
        acc[category].push(stock);
        return acc;
      }, {} as { [category: string]: Stock[] });
    });
  }

  addStock() {
    const payload = {
      symbol: this.newStock.symbol,
      category: this.newStock.category
    };
    this.stocksService.addStock(payload).subscribe(() => {
      this.loadStocks();
      this.showForm = false;
      this.newStock = { symbol: '', category: '' };
    });
  }
}
