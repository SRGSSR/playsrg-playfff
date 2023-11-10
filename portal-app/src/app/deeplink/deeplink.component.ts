import { Component, OnInit } from '@angular/core';
import { DeeplinkReport } from '../models/deeplink-report.model';
import { DeeplinkService } from './deeplink.service';

@Component({
  selector: 'app-deeplink',
  templateUrl: './deeplink.component.html',
  styleUrls: ['./deeplink.component.css']
})

export class DeeplinkComponent implements OnInit {
  deeplinkReports: DeeplinkReport[] = [];
  columns: string[] = [];

  constructor(private deeplinkService: DeeplinkService) { }

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
