import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DeeplinkComponent } from './deeplink/deeplink.component';
import { UpdateComponent } from './update/update.component';
import { AddUpdateComponent } from './update/add-update.component';

@NgModule({
  declarations: [
    AppComponent,
    DeeplinkComponent,
    UpdateComponent,
    AddUpdateComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
