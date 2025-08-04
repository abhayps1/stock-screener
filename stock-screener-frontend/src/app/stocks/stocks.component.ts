import { Component, OnInit, OnDestroy } from '@angular/core';
import { StocksService } from '../stocks.service';
import { Stock } from '../models/stock.model';
import { Subject, debounceTime, takeUntil } from 'rxjs';

@Component({
  selector: 'app-stocks',
  templateUrl: './stocks.component.html'
})
export class StocksComponent implements OnInit, OnDestroy {
  groupedStocks: { [category: string]: Stock[] } = {};
  newStock: Stock = {symbol : '', companyName: '', category: '', growwUrl: '', screenerUrl: '', trendlyneUrl: '' };
  showForm = false;
  hoveredStock: Stock | null = null;
  private destroy$ = new Subject<void>();
  private inputSubject = new Subject<string>();

  constructor(private stocksService: StocksService) {}

  ngOnInit(): void {
    this.loadStocks();
    this.setupDebouncedInput();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupDebouncedInput(): void {
    this.inputSubject
      .pipe(
        debounceTime(1000), // 1 second delay
        takeUntil(this.destroy$)
      )
      .subscribe(symbol => {
        if (symbol && symbol.trim()) {
          this.autoAddStock(symbol.trim());
        }
      });
  }

  onSymbolInput(event: any): void {
    const symbol = event.target.value;
    this.inputSubject.next(symbol);
  }

  private autoAddStock(symbol: string): void {
    const payload = {
      symbol: symbol,
      category: 'Auto Added' // Default category for auto-added stocks
    };
    
    this.stocksService.addStock(payload).subscribe({
      next: () => {
        this.loadStocks();
        this.newStock.symbol = ''; // Clear the input after successful addition
        console.log(`Stock ${symbol} added automatically`);
      },
      error: (error) => {
        console.error('Error adding stock:', error);
      }
    });
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
