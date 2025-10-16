import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IpoListComponent } from './ipo-list/ipo-list.component';
import { StocksComponent } from './stocks/stocks.component'; // Import your stocks component
import { ResultsComponent } from './results/results.component';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'stocks', component: StocksComponent },
  { path: 'ipo', component: IpoListComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }, // Optional: set Sector Analysis as default
  { path: "results", component: ResultsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
