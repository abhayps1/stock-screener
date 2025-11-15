import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockService } from '../services/stock-service';
import { ResultModel } from '../models/result.model';
import { Links } from '../reusable-component/links/links';

@Component({
  selector: 'app-result',
  imports: [CommonModule, Links],
  templateUrl: './result.html',
  styleUrl: './result.css'
})
export class Result implements OnInit {

  results = signal<ResultModel[]>([]);
  displayedResults = signal<ResultModel[]>([]);
  filterType = signal<string>('none');
  capFilter = signal<string>('all');
  sliderValue = signal<number>(0);
  isLoading: boolean = false;
  isTriggered: boolean = false;

  resultQuarter!: string;

  constructor(private stockService: StockService) {
    this.resultQuarter = stockService.getResultQuarter();
    console.log("Previous Quarter Name: ", this.resultQuarter);
  }

  ngOnInit(): void {
    this.getResults();
  }

  getResults(): void {
    this.stockService.getAllResults().subscribe(
      (data) => {
        this.results.set(data);
        this.sliderValue.set(0);
        this.applyFilter();
      },
      (error) => {
        console.error('Error fetching results:', error);
      }
    );
  }

  applyFilter(): void {
    let filtered = this.results();
    const filter = this.filterType();
    const cap = this.capFilter();
    if (filter === 'quarterly') {
      filtered = filtered.filter(result =>
        result.quarterlyRevenueCngPrcnt >= this.sliderValue() && result.quarterlyProfitCngPrcnt >= this.sliderValue()
      );
    } else if (filter === 'yearly') {
      filtered = filtered.filter(result =>
        result.yearlyRevenueCngPrcnt >= this.sliderValue() && result.yearlyProfitCngPrcnt >= this.sliderValue() && result.yearlyNetworthCngPrcnt >= this.sliderValue()
      );
    } else if (filter === 'both') {
      filtered = filtered.filter(result =>
        result.quarterlyRevenueCngPrcnt >= this.sliderValue() && result.quarterlyProfitCngPrcnt >= this.sliderValue() &&
        result.yearlyRevenueCngPrcnt >= this.sliderValue() && result.yearlyProfitCngPrcnt >= this.sliderValue() && result.yearlyNetworthCngPrcnt >= this.sliderValue()
      );
    } else {
      filtered = this.results();
    }
    // Apply cap filter
    if (cap === 'large') {
      filtered = filtered.filter(result => result.marketCap >= 50000);
    } else if (cap === 'mid') {
      filtered = filtered.filter(result => result.marketCap >= 10000 && result.marketCap < 50000);
    } else if (cap === 'small') {
      filtered = filtered.filter(result => result.marketCap < 10000);
    }
    this.displayedResults.set(filtered);
  }

  onFilterChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.filterType.set(target.value);
    this.applyFilter();
  }

  onCapFilterChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.capFilter.set(target.value);
    this.applyFilter();
  }

  onSliderChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.sliderValue.set(+target.value);
    this.applyFilter();
  }

  fetchResults(): void {
    this.isTriggered = true;
    this.isLoading = true;
    this.stockService.fetchResults().subscribe(
      (data) => {
        console.log('Fetch results response:', data);
        this.getResults(); // Refresh the results after fetching
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching results:', error);
        this.isLoading = false;
      }
    );
  }

}