import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { UpdateProfileRequest } from '../../models/auth.models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatProgressSpinnerModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);
  
  userProfile = {
    username: '',
    email: '',
    password: ''
  };

  isLoading = false;

  subscriptions = [
    { title: 'Titre du thème', description: 'Lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard...' },
    { title: 'Titre du thème', description: 'Lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard...' }
  ];

  ngOnInit(): void {
    // Load user profile data
    this.loadUserProfile();
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
      error: (error) => {
        console.log('Could not fetch fresh user data:', error);
        // Keep using cached data
      }
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
        console.log('✅ Profile updated successfully:', updatedUser);
        
        // Update local form with fresh data
        this.userProfile.username = updatedUser.username;
        this.userProfile.email = updatedUser.email;
        this.userProfile.password = ''; // Clear password field for security
        
        // Show success message
        this.snackBar.open('Profil mis à jour avec succès !', 'Fermer', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        
        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ Profile update failed:', error);
        
        // Show error message
        this.snackBar.open(error.message || 'Erreur lors de la mise à jour du profil', 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        
        this.isLoading = false;
      }
    });
  }

  private isValidForm(): boolean {
    // Basic validation
    if (!this.userProfile.username.trim()) {
      this.snackBar.open('Le nom d\'utilisateur est requis', 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return false;
    }

    if (!this.userProfile.email.trim()) {
      this.snackBar.open('L\'email est requis', 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return false;
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.userProfile.email)) {
      this.snackBar.open('Format d\'email invalide', 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return false;
    }

    return true;
  }

  onUnsubscribe(index: number): void {
    console.log('Unsubscribing from:', this.subscriptions[index]);
    // TODO: Implement unsubscribe logic
  }
}
