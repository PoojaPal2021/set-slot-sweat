import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

const routes: Routes = [];
/*
 AppRouting module is meant for routine related module definations.
*/
@NgModule({
  imports: [RouterModule.forRoot(routes),  ReactiveFormsModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
