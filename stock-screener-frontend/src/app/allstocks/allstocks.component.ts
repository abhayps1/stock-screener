import { Component, OnInit } from '@angular/core';
import { StockService } from '../services/stock.service';

@Component({
  selector: 'app-allstocks',
  templateUrl: './allstocks.component.html',
  styleUrls: ['./allstocks.component.css']
})
export class AllstocksComponent implements OnInit {
  stocks: any[] = [];

  constructor(private stockService: StockService) {}

  ngOnInit(): void {
    this.loadAllStocks();
  }

  loadAllStocks(): void {
    this.stockService.getAllStocksList().subscribe(
      (data) => {
        this.stocks = data;
        console.log('All stocks loaded:', data);
      },
      (error) => {
        console.error('Error fetching all stocks:', error);
      }
    );
  }
}
