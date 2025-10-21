import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockService } from '../../services/stock-service';
import { SearchedStockModel } from '../../models/searched-stock.model';
import { catchError, debounceTime, distinctUntilChanged, of, Subject, Subscription, switchMap } from 'rxjs';

@Component({
  selector: 'app-search',
  imports: [CommonModule, FormsModule],
  templateUrl: './search.html',
  styleUrl: './search.css'
})
export class Search implements OnInit, OnDestroy {
  @Output() stockSelected = new EventEmitter<SearchedStockModel>();

  searchTerm: string = '';
  stocks: SearchedStockModel[] = [];
  filteredStocks: SearchedStockModel[] = [];
  showResults: boolean = false;
  private searchSubject = new Subject<string>();
  private subscription: Subscription = new Subscription();

  constructor(private stockService: StockService) { }

  ngOnInit(): void {
    this.filteredStocks = this.stocks;

    this.subscription = this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(term => {
        if (term.trim()) {
          return this.stockService.searchStocks(term).pipe(
            catchError(error => {
              console.error('Error searching stocks:', error);
              return of([]);
            })
          );
        } else {
          return of([]);
        }
      })
    ).subscribe(results => {
      this.filteredStocks = results;
      this.showResults = results.length > 0;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onSearch(): void {
    this.searchSubject.next(this.searchTerm);
  }

  clearAndCollapse(): void {
    console.log('Clearing search input and collapsing results');
    this.searchTerm = '';
    this.filteredStocks = [];
    this.showResults = false;
  }
}
