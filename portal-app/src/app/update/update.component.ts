import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Update } from '../models/update.model';
import { UpdateService } from './update.service';

@Component({
  selector: 'app-user',
  templateUrl: './update.component.html',
  styles: []
})
export class UpdateComponent implements OnInit {
  updates: Update[];
  columns: string[];

  constructor(private router: Router, private updateService: UpdateService) {

  }

  ngOnInit() {
    this.columns = this.updateService.getColumns();

    this.updateService.getUpdates()
      .subscribe( data => {
        console.log(data);
        this.updates = data;
      });
  };

  deleteUpdate(update: Update): void {
    this.updateService.deleteUpdate(update)
      .subscribe( data => {
        this.updates = this.updates.filter(u => u !== update);
      })
  };

}
