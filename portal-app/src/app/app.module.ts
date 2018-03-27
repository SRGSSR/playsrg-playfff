import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { UpdateComponent } from './update/update.component';
import { AppRoutingModule } from './app.routing.module';
import {UpdateService} from './update/update.service';
import {HttpClientModule} from "@angular/common/http";

import {AddUpdateComponent} from './update/add-update.component';

@NgModule({
  declarations: [
    AppComponent,
    UpdateComponent,
    AddUpdateComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [UpdateService],
  bootstrap: [AppComponent]
})
export class AppModule { }
