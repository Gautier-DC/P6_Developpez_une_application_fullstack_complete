import { Component, inject, Input, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatSidenav } from '@angular/material/sidenav';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSidenavModule,
    MatListModule,
    RouterLink,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  @ViewChild('mobileDrawer') mobileDrawer!: MatSidenav;
  @Input() drawer!: MatSidenav;
  protected authService = inject(AuthService);

  onBurgerMenuClick(): void {
    if (this.drawer) {
      this.drawer.toggle();
    }
  }

  onLogoutClick(): void {
    this.authService.logout().subscribe({
      next: () => {
      },
      error: (error) => {
        console.error('❌ Logout failed:', error);
        // Even if error, user is still logged out locally
      },
    });
  }

  onMobileNavClick(): void {
    // Close the drawer when a navigation item is clicked
    if (this.mobileDrawer) {
      this.mobileDrawer.close();
    }
  }
}
