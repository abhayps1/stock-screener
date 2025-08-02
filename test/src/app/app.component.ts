import { Component } from '@angular/core';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexYAxis,
  ApexTitleSubtitle,
  ApexPlotOptions,
  ApexLegend
} from 'ng-apexcharts';

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  yaxis: ApexYAxis;
  title: ApexTitleSubtitle;
  plotOptions: ApexPlotOptions;
  colors: string[];
  legend: ApexLegend;
};

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public chartOptions: ChartOptions;

  constructor() {
    this.chartOptions = {
      series: [
        {
          name: "Revenue",
          data: [40153, 41698, 42623, 42115, 43321]
        }
      ],
      chart: {
        type: "bar",
        height: 350
      },
      colors: ['#28a745'],
      plotOptions: {
        bar: {
          columnWidth: '25%'
        }
      },
      legend: {
        show: false
      },
      yaxis: {
        show: false
      },
      title: {
        text: "Quarterly Financial Results"
      },
      xaxis: {
        categories: ["Jun '24", "Sep '24", "Dec '24", "Mar '25", "Jun '25"]
      }
    };
  }
}
