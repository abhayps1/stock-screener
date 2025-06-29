import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IpoListComponent } from './ipo-list/ipo-list.component';

const routes: Routes = [
  { path: 'ipo', component: IpoListComponent },
  // { path: '', redirectTo: '/ipo', pathMatch: 'full' } // Optional: set IPO as default
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
