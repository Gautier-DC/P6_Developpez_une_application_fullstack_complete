import { Component, Input, OnInit  } from '@angular/core';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

export interface HeaderConfig {
  showBackButton?: boolean;
  showBurgerMenu?: boolean;
  showNavigation?: boolean;
  showUserProfile?: boolean;
  showSeparator?: boolean;
  title?: string;
  logoSize?: 'small' | 'large';
  logoPosition?: 'left' | 'center';
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  @Input() config: HeaderConfig = {};
  
  // Default configuration
  private defaultConfig: HeaderConfig = {
    showBackButton: false,
    showBurgerMenu: false,
    showNavigation: true,
    showUserProfile: true,
    showSeparator: true,
    logoSize: 'small',
    logoPosition: 'left'
  };

  finalConfig: HeaderConfig = {};

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Merge default config with provided config
    this.finalConfig = { ...this.defaultConfig, ...this.config };
  }

  onBackClick(): void {
    // Navigate back or emit event to parent
    window.history.back();
    // Or use: this.router.navigateByUrl('/previous-route');
  }

  onBurgerMenuClick(): void {
    // Emit event to parent component to handle menu toggle
    // Or implement menu logic here
    console.log('Burger menu clicked');
  }

  onUserProfileClick(): void {
    // Handle user profile click
    console.log('User profile clicked');
  }

  onLogoClick(): void {
    this.router.navigate(['/']);
  }
}
