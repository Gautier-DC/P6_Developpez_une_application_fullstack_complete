import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd, RouterLink } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { filter } from 'rxjs/operators';
import { MatSidenavContainer, MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from './services/auth.service';
import { MinimalistHeaderComponent } from './components/minimalist-header/minimalist-header.component';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, HeaderComponent, MinimalistHeaderComponent, MatSidenavModule, MatListModule, MatIconModule, MatButtonModule, MatDividerModule],
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
  protected authService = inject(AuthService);
  title = 'front';
  showHeader = false;

  // Pages où le header ne doit pas apparaître
  private hiddenHeaderRoutes = ['/','/login', '/register'];

  constructor(private router: Router) {
    // Écouter les changements de route pour masquer/afficher le header
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const currentPath = event.url.split('?')[0]; // Remove query parameters
      this.showHeader = !this.hiddenHeaderRoutes.includes(currentPath);
    });
  }

  onLogoutClick(): void {
    this.authService.logout().subscribe({
      next: (response) => {
        console.log('✅ Logout successful:', response);
      },
      error: (error) => {
        console.error('❌ Logout failed:', error);
        // Even if error, user is still logged out locally
      }
    });
  }

  isHomePage(): boolean {
    return this.router.url === '/';
  }
}
