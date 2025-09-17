import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { HeaderComponent, HeaderConfig } from './components/header/header.component';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterOutlet, HeaderComponent],
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'front';
  showHeader = false;
  headerConfig: HeaderConfig = {};

  // Pages où le header ne doit pas apparaître
  private hiddenHeaderRoutes = ['/login', '/'];

  constructor(private router: Router) {
    // Écouter les changements de route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.showHeader = !this.hiddenHeaderRoutes.includes(event.url);
      this.updateHeaderConfig(event.url);
    });
  }

  private updateHeaderConfig(url: string): void {
    // Configuration par défaut
    this.headerConfig = {
      showBackButton: false,
      showBurgerMenu: false,
      showNavigation: true,
      showUserProfile: true,
      showSeparator: true,
      logoSize: 'small',
      logoPosition: 'left'
    };

    // Configuration spécifique par route
    switch (url) {
      case '/register':
        this.headerConfig = {
          ...this.headerConfig,
          showBackButton: true,
          showNavigation: false,
          showUserProfile: false,
        };
        break;
      case '/articles':
        this.headerConfig = {
          ...this.headerConfig,
          title: 'Articles'
        };
        break;
      case '/themes':
        this.headerConfig = {
          ...this.headerConfig,
          title: 'Thèmes'
        };
        break;
      case '/profile':
        this.headerConfig = {
          ...this.headerConfig,
          title: 'Mon Profil',
          showBackButton: true
        };
        break;
      default:
        // Garde la config par défaut
        break;
    }
  }
}
