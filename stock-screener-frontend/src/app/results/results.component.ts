import { Component, OnInit } from '@angular/core';
import { StockService } from '../services/stock.service';
import { Result } from '../models/result.model';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit {

  results: Result[] = [];
  isLoading: boolean = false;
  isTriggered: boolean = false;

  constructor(private stockService: StockService) { }

  ngOnInit(): void {
    this.getResults();
  }

  getResults(): void {
    this.stockService.getAllResults().subscribe(
      (data) => {
        this.results = data;
      },
      (error) => {
        console.error('Error fetching results:', error);
      }
    );
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
