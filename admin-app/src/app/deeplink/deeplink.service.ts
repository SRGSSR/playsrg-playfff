import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {DeeplinkReport} from '../models/deeplink-report.model';


const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({ providedIn: 'root' })
export class DeeplinkService {

  constructor(private http: HttpClient) { }

  private deeplinkReportUrl = '/api/v1/deeplink/report';

  public getDeeplinkReports() {
    return this.http.get<DeeplinkReport[]>(this.deeplinkReportUrl);
  }

  public deleteDeeplinkReport(deeplinkReport: DeeplinkReport) {
    return this.http.delete(this.deeplinkReportUrl + "/" + deeplinkReport.id);
  }

  public getColumns() {
    return [
      "id",
      "clientId",
      "clientTime",
      "count",
      "jsVersion",
      "url",
    ]
  }
}
