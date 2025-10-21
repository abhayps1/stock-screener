import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockService } from '../services/stock-service';
import { ResultModel } from '../models/result.model';

@Component({
  selector: 'app-result',
  imports: [CommonModule],
  templateUrl: './result.html',
  styleUrl: './result.css'
})
export class Result implements OnInit {

  results = signal<ResultModel[]>([]);
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