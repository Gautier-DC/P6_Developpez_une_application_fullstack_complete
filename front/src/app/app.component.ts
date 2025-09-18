import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
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

  // Pages où le header ne doit pas apparaître
  private hiddenHeaderRoutes = ['/','/login', '/register'];

  constructor(private router: Router) {
    // Écouter les changements de route pour masquer/afficher le header
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.showHeader = !this.hiddenHeaderRoutes.includes(event.url);
    });
  }
}
