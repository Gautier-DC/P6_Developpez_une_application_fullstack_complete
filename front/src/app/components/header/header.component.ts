import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  protected authService = inject(AuthService);

  // Back button functionality removed - now handled by separate BackButtonComponent

  onBurgerMenuClick(): void {
    // Emit event to parent component to handle menu toggle
    // Or implement menu logic here
    console.log('Burger menu clicked');
  }

  onUserProfileClick(): void {
    // Handle user profile click
    console.log('User profile clicked');
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
}
