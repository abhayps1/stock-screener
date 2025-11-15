import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResultModel } from '../../models/result.model';

@Component({
  selector: 'app-links',
  imports: [CommonModule],
  templateUrl: './links.html',
  styleUrl: './links.css'
})
export class Links {
  @Input() result!: ResultModel;
}
