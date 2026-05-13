import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { KidneyTransplantListComponent } from '../component/front/kidney-transplant/kidney-transplant-list/kidney-transplant-list.component';
import { KidneyTransplantDetailComponent } from '../component/front/kidney-transplant/kidney-transplant-detail/kidney-transplant-detail.component';
import { PostTransplantFollowUpComponent } from '../component/front/kidney-transplant/post-transplant-follow-up/follow-up-create/follow-up-create.component';
import { FollowUpDetailComponent } from '../component/front/kidney-transplant/post-transplant-follow-up/follow-up-detail/follow-up-detail.component';
import { FollowUpUpdateComponent } from '../component/front/kidney-transplant/post-transplant-follow-up/follow-up-update/follow-up-update.component';

const routes: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: KidneyTransplantListComponent },
  { path: 'kidney-transplant-detail/:id', component: KidneyTransplantDetailComponent },
  { path: 'post-transplant-follow-up/:id/follow-up-create', component: PostTransplantFollowUpComponent },
  { path: 'post-transplant-follow-up/:id/follow-up-detail/:fid', component: FollowUpDetailComponent },
  { path: 'post-transplant-follow-up/:id/follow-up-update/:fid', component: FollowUpUpdateComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class KidneyTransplantRoutingModule { }
