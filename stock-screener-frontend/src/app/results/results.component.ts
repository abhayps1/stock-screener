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

  constructor(private stockService: StockService) { }

  ngOnInit(): void {
    this.loadResults();
  }

  loadResults(): void {
    this.stockService.getAllResults().subscribe(
      (data) => {
        this.results = data;
      },
      (error) => {
        console.error('Error fetching results:', error);
      }
    );
  }

}
