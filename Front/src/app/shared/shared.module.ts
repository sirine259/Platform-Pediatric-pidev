import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Import CRUD Components
import { CrudListComponent } from '../component/shared/crud/crud-list/crud-list.component';

@NgModule({
  declarations: [
    CrudListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    RouterModule,
    CrudListComponent
  ]
})
export class SharedModule { }
