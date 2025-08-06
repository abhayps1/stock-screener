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
  companyTerm: string = ''; // New search variable
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
      .subscribe(companyTerm => {
        if (companyTerm && companyTerm.trim()) {
          this.searchStocks(companyTerm.trim());
        }
      });
  }

  onSymbolInput(event: any): void {
    const companyTerm = event.target.value;
    this.companyTerm = companyTerm; // Store in companyTerm instead of newStock.symbol
    this.inputSubject.next(companyTerm);
  }

  private searchStocks(companyTerm: string): void {
    console.log(`Making search request for company: "${companyTerm}"`);
    
    this.stocksService.searchStocks(companyTerm).subscribe({
      next: (searchResults) => {
        // Handle search results here
        console.log(`Search API Response for "${companyTerm}":`, searchResults);
        console.log(`Response type: ${typeof searchResults}`);
        console.log(`Response length: ${searchResults.length}`);
        
        // Log first 200 characters to see the HTML structure
        console.log(`First 200 characters:`, searchResults.substring(0, 200));
        
        // You can display search results or handle them as needed
        this.companyTerm = ''; // Clear the search input after search
      },
      error: (error) => {
        console.error('Error searching stocks:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url
        });
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
