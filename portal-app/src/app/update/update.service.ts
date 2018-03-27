import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Update} from '../models/update.model';


const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable()
export class UpdateService {

  constructor(private http: HttpClient) {
  }

  private updateUrl = '/api/v1/update';

  public getUpdates() {
    return this.http.get<Update[]>(this.updateUrl);
  }

  public deleteUpdate(update) {
    return this.http.delete(this.updateUrl + "/" + update.id);
  }

  public createUpdate(update) {
    return this.http.post(this.updateUrl, update);
  }

  public getColumns() {
    return [
      "id",
      "packageName",
      "version",
      "text",
      "mandatory",
    ]
  }
}
