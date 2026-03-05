import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { KidneyTransplantRoutingModule } from './kidney-transplant-routing.module';
import { KidneyTransplantDetailComponent } from '../component/front/kidney-transplant/kidney-transplant-detail/kidney-transplant-detail.component';
import { PostTransplantFollowUpComponent } from '../component/front/kidney-transplant/post-transplant-follow-up/follow-up-create/follow-up-create.component';
import { FollowUpDetailComponent } from '../component/front/kidney-transplant/post-transplant-follow-up/follow-up-detail/follow-up-detail.component';
import { FollowUpUpdateComponent } from '../component/front/kidney-transplant/post-transplant-follow-up/follow-up-update/follow-up-update.component';

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    RouterModule,
    KidneyTransplantRoutingModule,
    KidneyTransplantDetailComponent,
    PostTransplantFollowUpComponent,
    FollowUpDetailComponent,
    FollowUpUpdateComponent
  ],
  exports: [
  ]
})
export class KidneyTransplantModule { }
