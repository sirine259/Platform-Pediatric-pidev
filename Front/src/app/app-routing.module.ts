import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Home Component
import { HomeComponent } from './home/home/home.component';

// Guards
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  // Default route - redirect to home
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  
  // Home
  { path: 'home', component: HomeComponent },
  
  // Authentication Routes
  { 
    path: 'auth', 
    loadComponent: () => import('./home/auth/login/login.component').then(m => m.LoginComponent)
  },
  { 
    path: 'auth/login', 
    loadComponent: () => import('./home/auth/login/login.component').then(m => m.LoginComponent)
  },
  { 
    path: 'auth/signup', 
    loadComponent: () => import('./home/auth/signup/signup.component').then(m => m.SignupComponent)
  },
  { 
    path: 'auth/forgot-password', 
    loadComponent: () => import('./home/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },

  // Portail de la plateforme (choix Forum / Kidney Transplant)
  {
    path: 'platform',
    canActivate: [AuthGuard],
    loadComponent: () => import('./home/platform/platform-portal.component').then(m => m.PlatformPortalComponent)
  },
  
  // Forum Routes - temporairement sans AuthGuard pour démonstration
  { 
    path: 'forum', 
    children: [
      { path: '', loadComponent: () => import('./component/front/forum/forum.component').then(m => m.ForumComponent) },
      { path: 'list', loadComponent: () => import('./component/front/forum/forum-list/forum-list.component').then(m => m.ForumListComponent) },
      { path: 'forum/:id', loadComponent: () => import('./component/front/forum/forum-detail/forum-detail.component').then(m => m.ForumDetailComponent) },
      { path: 'post/:id', loadComponent: () => import('./component/front/forum/post/post-detail/post-detail.component').then(m => m.PostDetailComponent) },
      { path: 'post-detail/:id', loadComponent: () => import('./component/front/forum/post/post-detail/post-detail.component').then(m => m.PostDetailComponent) },
      { path: 'create', loadComponent: () => import('./component/front/forum/forum-create/forum-create.component').then(m => m.ForumCreateComponent) },
      { path: 'post-create', loadComponent: () => import('./component/front/forum/post/post-create/post-create.component').then(m => m.PostCreateComponent) },
      { path: 'post-update/:id', loadComponent: () => import('./component/front/forum/post/post-update/post-update.component').then(m => m.PostUpdateComponent) }
    ]
  },
  
  // Kidney Transplant Routes - temporairement sans AuthGuard pour démonstration
  { 
    path: 'kidney-transplant', 
    children: [
      { path: '', redirectTo: 'list', pathMatch: 'full' },
      { path: 'list', loadComponent: () => import('./component/front/kidney-transplant/kidney-transplant-list/kidney-transplant-list.component').then(m => m.KidneyTransplantListComponent) },
      { path: 'create', loadComponent: () => import('./component/front/kidney-transplant/kidney-transplant-create/kidney-transplant-create.component').then(m => m.KidneyTransplantCreateComponent) },
      { path: 'kidney-transplant-detail/:id', loadComponent: () => import('./component/front/kidney-transplant/kidney-transplant-detail/kidney-transplant-detail.component').then(m => m.KidneyTransplantDetailComponent) },
      { path: 'post-transplant-follow-up/:id/follow-up-create', loadComponent: () => import('./component/front/kidney-transplant/post-transplant-follow-up/follow-up-create/follow-up-create.component').then(m => m.PostTransplantFollowUpComponent) },
      { path: 'post-transplant-follow-up/:id/follow-up-detail/:fid', loadComponent: () => import('./component/front/kidney-transplant/post-transplant-follow-up/follow-up-detail/follow-up-detail.component').then(m => m.FollowUpDetailComponent) },
      { path: 'post-transplant-follow-up/:id/follow-up-update/:fid', loadComponent: () => import('./component/front/kidney-transplant/post-transplant-follow-up/follow-up-update/follow-up-update.component').then(m => m.FollowUpUpdateComponent) }
    ] 
  },
  
  // Patient Routes - protégées par AuthGuard
  { 
    path: 'front/patient', 
    canActivate: [AuthGuard],
    loadComponent: () => import('./component/front/patient/patient.component').then(m => m.PatientComponent)
  },
  
  // Dashboard Routes
  { path: 'dashboard', loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
  
  // Role-specific Routes
  { path: 'admin', redirectTo: '/admin/dashboard' },
  { path: 'admin/dashboard', loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
  { path: 'doctor', redirectTo: '/doctor/dashboard' },
  { path: 'doctor/dashboard', loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
  { path: 'patient', redirectTo: '/patient/dashboard' },
  { path: 'patient/dashboard', loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
  
  // Wildcard route
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
