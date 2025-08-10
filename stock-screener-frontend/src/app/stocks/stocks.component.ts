import { Component, OnInit, OnDestroy } from '@angular/core';
import { StocksService } from '../stocks.service';
import { Stock } from '../models/stock.model';
import { Subject, debounceTime, takeUntil } from 'rxjs';

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
    this.stocksService.searchStocks(companyTerm).subscribe({
      next: (htmlResponse) => {
        // You can implement your custom logic here
        this.searchResponseHTML = htmlResponse;
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

  // Handle click events from injected search HTML. Uses event delegation to capture anchor clicks.
  onSearchResultClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;
    if (!target) { return; }

    // Walk up to nearest anchor tag if clicked inside nested elements
    let element: HTMLElement | null = target;
    while (element && element.tagName.toLowerCase() !== 'a') {
      element = element.parentElement;
    }

    if (element && element.tagName.toLowerCase() === 'a') {
      event.preventDefault();
      const referenceUrl = (element as HTMLAnchorElement).getAttribute('href') || '';
      if (!referenceUrl) { return; }

      this.stocksService.addStock(referenceUrl).subscribe({
        next: () => {
          // Optionally refresh list and clear results
          this.loadStocks();
          // Keep or clear search results depending on UX; here we clear
          this.searchResponseHTML = '';
        },
        error: (error) => {
          console.error('Failed to add stock via reference URL:', error);
        }
      });
    }
  }

  loadStocks() {
    this.stocksService.getStocks().subscribe(stocks => {
      this.stocks = stocks.map(stock => {
        if (stock.indicatorData) {
          try {
            const indicators = JSON.parse(stock.indicatorData);
            stock['rsi'] = indicators['Day RSI'] || '';
            stock['mfi'] = indicators['Day MFI'] || '';
          } catch (e) {
            console.error('Error parsing indicatorData JSON', e);
            stock['rsi'] = '';
            stock['mfi'] = '';
          }
        } else {
          stock['rsi'] = '';
          stock['mfi'] = '';
        }
        return stock;
      });
    });
  }

}
