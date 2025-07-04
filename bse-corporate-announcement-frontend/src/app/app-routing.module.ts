import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IpoListComponent } from './ipo-list/ipo-list.component';
import { StocksComponent } from './stocks/stocks.component'; // Import your stocks component

const routes: Routes = [
  { path: 'stocks', component: StocksComponent },
  { path: 'ipo', component: IpoListComponent },
  { path: '', redirectTo: '/stocks', pathMatch: 'full' } // Optional: set Sector Analysis as default
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
