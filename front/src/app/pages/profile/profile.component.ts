import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { UpdateProfileRequest } from '../../models/auth.models';
import { CardComponent } from '../../components/card/card.component';
import { ThemeService } from '../../services/theme.service';
import { Theme } from '../../models/article.models';
import { PasswordValidationDirective } from '../../directives/password-validation.directive';
import { getPasswordErrorMessage } from '../../validators/password.validator';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatTooltipModule,
    CardComponent,
    PasswordValidationDirective,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private themeService = inject(ThemeService);
  private snackBar = inject(MatSnackBar);

  userProfile = {
    username: '',
    email: '',
    password: '',
  };

  isLoading = false;
  isLoadingSubscriptions = false;
  subscribedThemes: Theme[] = [];

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadUserSubscriptions();
  }

  loadUserProfile(): void {
    // Get current user data from auth service
    const currentUser = this.authService.currentUser();
    if (currentUser) {
      this.userProfile.username = currentUser.username || '';
      this.userProfile.email = currentUser.email || '';
    }

    // Also try to fetch fresh user data from server
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.userProfile.username = user.username || '';
        this.userProfile.email = user.email || '';
      },
      error: () => {
        // Keep using cached data
      },
    });
  }

  onSaveProfile(): void {
    if (!this.isValidForm()) {
      return;
    }

    this.isLoading = true;

    // Prepare update data - only send fields that have values
    const updateData: UpdateProfileRequest = {};

    if (this.userProfile.username.trim()) {
      updateData.username = this.userProfile.username.trim();
    }

    if (this.userProfile.email.trim()) {
      updateData.email = this.userProfile.email.trim();
    }

    if (this.userProfile.password.trim()) {
      updateData.password = this.userProfile.password.trim();
    }

    // Call the API
    this.authService.updateProfile(updateData).subscribe({
      next: (updatedUser) => {

        // Update local form with fresh data
        this.userProfile.username = updatedUser.username;
        this.userProfile.email = updatedUser.email;
        this.userProfile.password = ''; // Clear password field for security

        // Show success message
        this.snackBar.open('Profil mis à jour avec succès !', 'Fermer', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });

        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ Profile update failed:', error);

        // Show error message
        this.snackBar.open(
          error.message || 'Erreur lors de la mise à jour du profil',
          'Fermer',
          {
            duration: 5000,
            panelClass: ['error-snackbar'],
          }
        );

        this.isLoading = false;
      },
    });
  }

  private isValidForm(): boolean {
    // Basic validation
    if (!this.userProfile.username.trim()) {
      this.snackBar.open("Le nom d'utilisateur est requis", 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar'],
      });
      return false;
    }

    if (!this.userProfile.email.trim()) {
      this.snackBar.open("L'email est requis", 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar'],
      });
      return false;
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.userProfile.email)) {
      this.snackBar.open("Format d'email invalide", 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar'],
      });
      return false;
    }

    // Password validation (only if password is provided)
    if (this.userProfile.password.trim()) {
      const password = this.userProfile.password.trim();

      // Validation des critères du mot de passe
      if (
        password.length < 8 ||
        !/\d/.test(password) ||
        !/[a-z]/.test(password) ||
        !/[A-Z]/.test(password) ||
        !/[@#$%^&+=!?.,:;()\[\]{}|\-_~`]/.test(password)
      ) {
        this.snackBar.open(
          'Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial',
          'Fermer',
          {
            duration: 5000,
            panelClass: ['error-snackbar'],
          }
        );
        return false;
      }
    }

    return true;
  }

  loadUserSubscriptions(): void {
    this.isLoadingSubscriptions = true;

    this.themeService.getUserSubscriptions().subscribe({
      next: (subscriptionIds) => {
        if (subscriptionIds.length === 0) {
          this.subscribedThemes = [];
          this.isLoadingSubscriptions = false;
          return;
        }

        // Get all themes and filter by subscribed IDs
        this.themeService.getAllThemes().subscribe({
          next: (allThemes) => {
            this.subscribedThemes = allThemes.filter((theme) =>
              subscriptionIds.includes(theme.id)
            );
            this.isLoadingSubscriptions = false;
          },
          error: (error) => {
            console.error('Error loading themes:', error);
            this.isLoadingSubscriptions = false;
          },
        });
      },
      error: (error) => {
        console.error('Error loading subscriptions:', error);
        this.isLoadingSubscriptions = false;
      },
    });
  }

  onUnsubscribe(theme: Theme): void {
    this.themeService.unsubscribeFromTheme(theme.id).subscribe({
      next: () => {
        this.subscribedThemes = this.subscribedThemes.filter(
          (t) => t.id !== theme.id
        );
        this.snackBar.open(`Désabonné du thème "${theme.name}"`, 'Fermer', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });
      },
      error: (error) => {
        console.error('Error unsubscribing:', error);
        this.snackBar.open('Erreur lors du désabonnement', 'Fermer', {
          duration: 3000,
          panelClass: ['error-snackbar'],
        });
      },
    });
  }

  getPasswordErrorMessage(passwordField: any): string {
    if (passwordField && passwordField.errors) {
      return getPasswordErrorMessage(passwordField.errors);
    }
    return '';
  }
}
