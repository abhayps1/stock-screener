import { Component, OnInit } from '@angular/core';
import { StocksService } from '../stocks.service';
import { Stock } from '../models/stock.model';

@Component({
  selector: 'app-stocks',
  templateUrl: './stocks.component.html'
})
export class StocksComponent implements OnInit {
  groupedStocks: { [category: string]: Stock[] } = {};
  newStock: Stock = { stockName: '', stockCategory: '', growwUrl: '', screenerUrl: '', trendlyneUrl: '' };
  showForm = false;
  hoveredStock: Stock | null = null;

  constructor(private stocksService: StocksService) {}

  ngOnInit(): void {
    this.loadStocks();
  }

  loadStocks() {
    this.stocksService.getStocks().subscribe(stocks => {
      this.groupedStocks = stocks.reduce((acc, stock) => {
        const category = stock.stockCategory || 'Uncategorized';
        if (!acc[category]) {
          acc[category] = [];
        }
        acc[category].push(stock);
        return acc;
      }, {} as { [category: string]: Stock[] });
    });
  }

  addStock() {
    this.stocksService.addStock(this.newStock).subscribe(() => {
      this.newStock = { stockName: '', stockCategory: '', growwUrl: '', screenerUrl: '', trendlyneUrl: '' };
      this.showForm = false;
      this.loadStocks();
    });
  }
}
