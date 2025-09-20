import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { StockService } from '../services/stock.service';
import { Stock } from '../models/stock.model';
import { Watchlist } from '../models/watchlist.model';
import { Subject } from 'rxjs';
import { SearchComponent } from '../search/search.component';

@Component({
  selector: 'app-stocks',
  templateUrl: './stocks.component.html'
})
export class StocksComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild(SearchComponent) searchComponent!: SearchComponent;
  private searchComponentReady = false;

  stocks: Stock[] = [];
  watchlists: String[] = [];
  companyTerm: string = '';
  searchResponseHTML: string = '';
  showForm = false;
  hoveredStock: Stock | null = null;
  private destroy$ = new Subject<void>();
  private inputSubject = new Subject<string>();

  showCreateWatchlistModal: boolean = false;
  newWatchlistName: string = '';

  constructor(private stockService: StockService) { }

  ngOnInit(): void {
    this.loadWatchlist();
    this.loadStocks();
  }

  ngAfterViewInit(): void {
    this.searchComponentReady = true;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadStocks(): void {
    this.stockService.getStocks().subscribe(stocks => {
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
    },
      error => {
        console.error('Error loading stocks:', error);
      }
    );
  }

  onStockSelected(stock: any): void {
    this.addStock(stock);
  }

  addStock(stock: any): void {
    this.stockService.addStock(stock).subscribe(
      response => {
        console.log(response);
        if (this.searchComponentReady && this.searchComponent && this.searchComponent.clearAndCollapse) {
          console.log('Clearing search input and collapsing results from parent component');
          this.searchComponent.clearAndCollapse();
        }
        this.loadStocks();
      },
      error => {
        console.error('Error adding stock:', error);
      }
    );
  }

  openCreateWatchlistModal(): void {
    this.showCreateWatchlistModal = true;
  }

  closeCreateWatchlistModal(): void {
    this.showCreateWatchlistModal = false;
    this.newWatchlistName = '';
  }

  createWatchlist(): void {
    if (this.newWatchlistName.trim()) {

      this.stockService.createWatchlist(this.newWatchlistName).subscribe(
        (response: any) => {
          this.watchlists.push(this.newWatchlistName);
          this.closeCreateWatchlistModal();
        },
        (error: any) => {
          console.error('Error adding to watchlist:', error);
        }
      );
    }
  }

  loadWatchlist(): void {
    this.stockService.getAllWatchlists().subscribe(
      (watchlists: String[]) => {
        this.watchlists = watchlists;
      },
      (error: any) => {
        console.error('Error loading watchlist:', error);
      }
    );
  }
}