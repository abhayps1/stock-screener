import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StockService } from '../services/stock.service';
import { Link } from '../models/link.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  linkForm: FormGroup;
  links: Link[] = [];

  constructor(private fb: FormBuilder, private stockService: StockService) {
    this.linkForm = this.fb.group({
      description: ['', Validators.required],
      url: ['', [Validators.required, Validators.pattern('https?://.+')]]
    });
  }

  ngOnInit(): void {
    this.loadLinks();
  }

  saveLink(): void {
    if (this.linkForm.valid) {
      const link: Link = this.linkForm.value;
      this.stockService.saveLink(link).subscribe({
        next: (response) => {
          console.log('Link saved:', response);
          this.linkForm.reset();
          this.loadLinks();
        },
        error: (error) => {
          console.error('Error saving link:', error);
        }
      });
    }
  }

  loadLinks(): void {
    this.stockService.getAllLinks().subscribe({
      next: (data) => {
        this.links = data;
      },
      error: (error) => {
        console.error('Error loading links:', error);
      }
    });
  }
}
