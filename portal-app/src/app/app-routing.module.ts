import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UpdateComponent } from './update/update.component';
import { AddUpdateComponent } from './update/add-update.component';
import { DeeplinkComponent } from './deeplink/deeplink.component';

const routes: Routes = [
  { path: 'updates', component: UpdateComponent },
  { path: 'add_update', component: AddUpdateComponent },
  { path: 'deeplink', component: DeeplinkComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
