import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import {DeeplinkReport} from '../models/deeplink-report.model';
import { DeeplinkService } from './deeplink.service';

@Component({
  selector: 'app-user',
  templateUrl: './deeplink.component.html',
  styles: []
})
export class DeeplinkComponent implements OnInit {
  deeplinkReports: DeeplinkReport[];
  columns: string[];

  constructor(private router: Router, private deeplinkService: DeeplinkService) {

  }

  ngOnInit() {
    this.columns = this.deeplinkService.getColumns();

    this.deeplinkService.getDeeplinkReports()
      .subscribe( data => {
        console.log(data);
        this.deeplinkReports = data;
      });
  };

  deleteDeeplinkReport(deeplinkReport: DeeplinkReport): void {
    this.deeplinkService.deleteDeeplinkReport(deeplinkReport)
      .subscribe( data => {
        this.deeplinkReports = this.deeplinkReports.filter(dr => dr !== deeplinkReport);
      })
  };

}
