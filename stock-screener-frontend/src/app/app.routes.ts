import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Allstocks } from './allstocks/allstocks';
import { Stock } from './stock/stock';
import { Result } from './result/result';

export const routes: Routes = [
    { path: 'home', component: Home },
    { path: 'allStocks', component: Allstocks },
    { path: 'stocks', component: Stock},
    { path: 'results', component: Result },
    { path: 'sector-analysis', redirectTo: '/home', pathMatch: 'full' },
    { path: 'ipo', redirectTo: '/home', pathMatch: 'full' },
    { path: 'news', redirectTo: '/home', pathMatch: 'full' },
    { path: 'holding', redirectTo: '/home', pathMatch: 'full' },
    { path: '', redirectTo: '/home', pathMatch: 'full' }
];
