import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MLDashboardComponent } from './ml-dashboard/ml-dashboard.component';
import { MLClusteringComponent } from './ml-clustering/ml-clustering.component';
import { MLClassificationComponent } from './ml-classification/ml-classification.component';
import { MLPredictionComponent } from './ml-prediction/ml-prediction.component';
import { MLRecommendationComponent } from './ml-recommendation/ml-recommendation.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: MLDashboardComponent },
  { path: 'clustering', component: MLClusteringComponent },
  { path: 'classification', component: MLClassificationComponent },
  { path: 'prediction', component: MLPredictionComponent },
  { path: 'recommendation', component: MLRecommendationComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MLRoutingModule { }
