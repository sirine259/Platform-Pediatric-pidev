import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ForumListComponent } from '../component/front/forum/forum-list/forum-list.component';
import { ForumDetailComponent } from '../component/front/forum/forum-detail/forum-detail.component';
import { PostCreateComponent } from '../component/front/forum/post/post-create/post-create.component';
import { PostUpdateComponent } from '../component/front/forum/post/post-update/post-update.component';
import { PostDetailComponent } from '../component/front/forum/post/post-detail/post-detail.component';

const routes: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: ForumListComponent },
  { path: 'forum/:id', component: ForumDetailComponent },
  { path: 'post/:id', component: PostDetailComponent },
  { path: 'post-detail/:id', component: PostDetailComponent },
  { path: 'create', component: PostCreateComponent },
  { path: 'post-update/:id', component: PostUpdateComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class ForumRoutingModule { }
