import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Update } from '../models/update.model';
import { UpdateService } from './update.service';

@Component({
  selector: 'app-add-update',
  templateUrl: './add-update.component.html',
  styleUrls: ['./add-update.component.css']
})

export class AddUpdateComponent {

  update: Update = new Update();

  constructor(private router: Router, private updateService: UpdateService) { }

  createUpdate(): void {
    this.updateService.createUpdate(this.update)
        .subscribe( data => {
          alert("Update message created or updated.");
          this.router.navigate(['/updates']);
        },
        err => {
          alert("Please complete all fields.");
        });

  };

}
