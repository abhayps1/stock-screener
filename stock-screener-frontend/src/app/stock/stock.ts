import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Search } from '../reusable-component/search/search';
import { Subject } from 'rxjs';
import { StockService } from '../services/stock-service';
import { StockModel } from '../models/stock.model';

@Component({
  selector: 'app-stock',
  imports: [CommonModule, FormsModule, Search],
  templateUrl: './stock.html',
  styleUrl: './stock.css'
})
export class Stock implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild(Search) searchComponent!: Search;
  private searchComponentReady = false;

  stocks: StockModel[] = [];
  watchlists = signal<String[]>([]);
  selectedWatchlist = signal<string>('');
  companyTerm: string = '';
  searchResponseHTML: string = '';
  showForm = false;
  hoveredStock: StockModel | null = null;
  private destroy$ = new Subject<void>();
  private inputSubject = new Subject<string>();

  showCreateWatchlistModal: boolean = false;
  newWatchlistName: string = '';
  watchlistErrorMessage: string = '';

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

  createWatchlistByName(): void {
    if (this.newWatchlistName.trim()) {
      
      this.stockService.createWatchlistByName(this.newWatchlistName.toUpperCase()).subscribe(
        (response: any) => {
          this.watchlists.update(list => [...list, this.newWatchlistName]);
          this.selectedWatchlist.set(this.newWatchlistName);
          this.closeCreateWatchlistModal();
        },
        (error: any) => {
          console.error('Error creating watchlist:', error);
          alert('Watchlist already exists');
        }
      );
    }
  }

  selectWatchlist(watchlistName: String): void {
    this.selectedWatchlist.set(watchlistName.toString());
  }

  deleteWatchlistByName(watchlistName: String): void {
    this.stockService.deleteWatchlistByName(watchlistName).subscribe(
      (response: any) => {
        this.watchlists.update(list => list.filter(w => w !== watchlistName));
        if (this.selectedWatchlist() === watchlistName.toString()) {
          this.selectedWatchlist.set('');
        }
      },
      (error: any) => {
        console.error('Error deleting watchlist:', error);
      }
    );
  }

  loadWatchlist(): void {
    this.stockService.getAllWatchlistNames().subscribe(
      (watchlists: String[]) => {
        console.log('Loaded watchlists:', watchlists);
        this.watchlists.set(watchlists);
      },
      (error: any) => {
        console.error('Error loading watchlist:', error);
      }
    );
  }
}