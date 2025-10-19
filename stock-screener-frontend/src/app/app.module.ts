import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AnnouncementsComponent } from './announcements/announcements.component';
import { IpoListComponent } from './ipo-list/ipo-list.component';
import { StocksComponent } from './stocks/stocks.component';
import { ResultsComponent } from './results/results.component';
import { SearchComponent } from './search/search.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './home/home.component';
import { AllstocksComponent } from './allstocks/allstocks.component';
@NgModule({
  declarations: [
    AppComponent,
    AnnouncementsComponent,
    IpoListComponent,
    StocksComponent,
    ResultsComponent,
    SearchComponent,
    HomeComponent,
    AllstocksComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
