import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { Update } from '../models/update.model';
import { UpdateService } from './update.service';

@Component({
  templateUrl: './add-update.component.html'
})
export class AddUpdateComponent {

  update: Update = new Update();

  constructor(private router: Router, private updateService: UpdateService) {

  }

  createUpdate(): void {
    this.updateService.createUpdate(this.update)
        .subscribe( data => {
          alert("Update created successfully.");
        });

  };

}
