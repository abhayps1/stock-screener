import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { SearchService } from '../services/search.service';
import { SearchedStock } from '../models/searched-stock.model';
import { Subject, Subscription, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit, OnDestroy {
  @Output() stockSelected = new EventEmitter<SearchedStock>();

  searchTerm: string = '';
  stocks: SearchedStock[] = [];
  filteredStocks: SearchedStock[] = [];
  showResults: boolean = false;
  private searchSubject = new Subject<string>();
  private subscription: Subscription = new Subscription();

  constructor(private searchService: SearchService) { }

  ngOnInit(): void {
    this.filteredStocks = this.stocks;

    this.subscription = this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
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
