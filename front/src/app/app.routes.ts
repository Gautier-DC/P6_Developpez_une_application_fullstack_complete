import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';

export const routes: Routes = [
  // Public route (no auth needed)
  {
    path: '',
    component: HomeComponent,
    canActivate: [guestGuard]
  },
  
  // Guest routes (login, register) with guestGuard 
  { 
    path: 'register', 
    component: RegisterComponent,
    canActivate: [guestGuard]
  },
  
  { 
    path: 'login', 
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
    canActivate: [guestGuard]
  },
  {
    path: 'articles',
    loadComponent: () => import('./pages/articles/articles.component').then(m => m.ArticlesComponent),
    canActivate: [authGuard]
  },
  {
    path: 'articles/:id',
    loadComponent: () => import('./pages/article-detail/article-detail.component').then(m => m.ArticleDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: 'create-article',
    loadComponent: () => import('./pages/create-article/create-article.component').then(m => m.CreateArticleComponent),
    canActivate: [authGuard]
  },
  {
    path: 'themes',
    loadComponent: () => import('./pages/themes/themes.component').then(m => m.ThemesComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'profile', 
    loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  
  // Page 404 - Home redirection
  { 
    path: '**', 
    redirectTo: '' 
  }
];