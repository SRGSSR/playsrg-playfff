import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UpdateComponent } from './update/update.component';
import {AddUpdateComponent} from './update/add-update.component';

const routes: Routes = [
  { path: 'updates', component: UpdateComponent },
  { path: 'add_update', component: AddUpdateComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ],
  declarations: []
})
export class AppRoutingModule { }
