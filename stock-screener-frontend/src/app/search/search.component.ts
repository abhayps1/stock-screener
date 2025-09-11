import { Component, OnInit, OnDestroy } from '@angular/core';
import { SearchService } from '../services/search.service';
import { SearchedStock } from '../models/searched-stock.model';
import { Subject, Subscription, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { StockService } from '../services/stock.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit, OnDestroy {
  searchTerm: string = '';
  stocks: SearchedStock[] = [];
  filteredStocks: SearchedStock[] = [];
  private searchSubject = new Subject<string>();
  private subscription: Subscription = new Subscription();

  constructor(private searchService: SearchService, private stockService : StockService) { }

  ngOnInit(): void {
    this.filteredStocks = this.stocks;

    this.subscription = this.searchSubject.pipe(
      debounceTime(500), // Wait 1 second after user stops typing
      distinctUntilChanged(), // Only emit if value has changed
      switchMap(term => {
        if (term.trim()) {
          return this.searchService.searchStocks(term).pipe(
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
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onSearch(): void {
    this.searchSubject.next(this.searchTerm);
  }

  addStock(stock: SearchedStock): void {
    this.stockService.addStock(stock).subscribe(
      response => {
        console.log('Stock added successfully:', response);
        // Optionally, show a success message or update UI
      },
      error => {
        console.error('Error adding stock:', error);
        // Optionally, show an error message
      }
    );
  }
}
