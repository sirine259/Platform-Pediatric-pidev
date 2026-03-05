import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProgressModule } from '@coreui/angular';

import { ForumRoutingModule } from './forum-routing.module';
import { ForumListComponent } from '../component/front/forum/forum-list/forum-list.component';
import { ForumDetailComponent } from '../component/front/forum/forum-detail/forum-detail.component';
import { PostUpdateComponent } from '../component/front/forum/post/post-update/post-update.component';
import { PostCreateComponent } from '../component/front/forum/post/post-create/post-create.component';
import { PostDetailComponent } from '../component/front/forum/post/post-detail/post-detail.component';
import { ForumComponent } from '../component/front/forum/forum.component';
import { MaterialModule } from '../shared/material.module';


@NgModule({
  declarations: [
    ForumComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ProgressModule,
    ForumRoutingModule,
    MaterialModule,
    PostCreateComponent,
    PostDetailComponent,
    ForumListComponent,
    ForumDetailComponent,
    PostUpdateComponent
  ],
  exports: [
    ForumComponent
  ]
})
export class ForumModule { }
