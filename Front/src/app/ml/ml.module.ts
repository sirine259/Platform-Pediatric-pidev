import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { MLRoutingModule } from './ml-routing.module';
import { MLDashboardComponent } from './ml-dashboard/ml-dashboard.component';
import { MLClusteringComponent } from './ml-clustering/ml-clustering.component';
import { MLClassificationComponent } from './ml-classification/ml-classification.component';
import { MLPredictionComponent } from './ml-prediction/ml-prediction.component';
import { MLRecommendationComponent } from './ml-recommendation/ml-recommendation.component';
import { MaterialModule } from '../shared/material.module';

@NgModule({
  declarations: [
    MLDashboardComponent,
    MLClusteringComponent,
    MLClassificationComponent,
    MLPredictionComponent,
    MLRecommendationComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MLRoutingModule,
    MaterialModule
  ],
  exports: [
    MLDashboardComponent
  ]
})
export class MLModule { }
