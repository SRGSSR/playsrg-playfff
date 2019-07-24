import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { UpdateComponent } from './update/update.component';
import { DeeplinkComponent } from './deeplink/deeplink.component';
import { AppRoutingModule } from './app.routing.module';
import { UpdateService } from './update/update.service';
import { DeeplinkService } from './deeplink/deeplink.service';
import { HttpClientModule } from "@angular/common/http";

import {AddUpdateComponent} from './update/add-update.component';

@NgModule({
  declarations: [
    AppComponent,
    UpdateComponent,
    AddUpdateComponent,
    DeeplinkComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [UpdateService, DeeplinkService],
  bootstrap: [AppComponent]
})
export class AppModule { }
