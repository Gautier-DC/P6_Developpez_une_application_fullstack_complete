import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ThemeService } from '../../services/theme.service';
import { Theme } from '../../models/article.models';
import { CardComponent } from '../../components/card/card.component';
import { PageLayoutComponent } from 'src/app/components/page-layout/page-layout.component';

@Component({
  selector: 'app-themes',
  standalone: true,
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
    CardComponent,
    PageLayoutComponent
  ],
  templateUrl: './themes.component.html',
  styleUrls: ['./themes.component.scss']
})
export class ThemesComponent implements OnInit {
  private themeService = inject(ThemeService);

  themes: Theme[] = [];
  subscribedThemes: Set<number> = new Set();
  isLoading = true;
  processingThemes: Set<number> = new Set();

  ngOnInit(): void {
    this.loadThemes();
    this.loadUserSubscriptions();
  }

  loadThemes(): void {
    this.isLoading = true;
    this.themeService.getAllThemes().subscribe({
      next: (themes) => {
        this.themes = themes;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading themes:', error);
        this.isLoading = false;
      }
    });
  }

  loadUserSubscriptions(): void {
    this.themeService.getUserSubscriptions().subscribe({
      next: (subscriptions) => {
        this.subscribedThemes = new Set(subscriptions);
      },
      error: (error) => {
        console.error('Error loading user subscriptions:', error);
      }
    });
  }

  onSubscribeToggle(theme: Theme): void {
    if (this.processingThemes.has(theme.id)) {
      return;
    }

    this.processingThemes.add(theme.id);
    const isSubscribed = this.subscribedThemes.has(theme.id);

    if (isSubscribed) {
      this.unsubscribeFromTheme(theme);
    } else {
      this.subscribeToTheme(theme);
    }
  }

  private subscribeToTheme(theme: Theme): void {
    this.themeService.subscribeToTheme(theme.id).subscribe({
      next: () => {
        this.subscribedThemes.add(theme.id);
        this.processingThemes.delete(theme.id);
      },
      error: (error) => {
        console.error('Error subscribing to theme:', error);
        this.processingThemes.delete(theme.id);
      }
    });
  }

  private unsubscribeFromTheme(theme: Theme): void {
    this.themeService.unsubscribeFromTheme(theme.id).subscribe({
      next: () => {
        this.subscribedThemes.delete(theme.id);
        this.processingThemes.delete(theme.id);
      },
      error: (error) => {
        console.error('Error unsubscribing from theme:', error);
        this.processingThemes.delete(theme.id);
      }
    });
  }

  isSubscribed(themeId: number): boolean {
    return this.subscribedThemes.has(themeId);
  }

  isProcessing(themeId: number): boolean {
    return this.processingThemes.has(themeId);
  }

  getButtonText(theme: Theme): string {
    if (this.isProcessing(theme.id)) {
      return this.isSubscribed(theme.id) ? 'Désabonnement...' : 'Abonnement...';
    }
    return this.isSubscribed(theme.id) ? 'Déjà abonné' : "S'abonner";
  }

  getButtonColor(theme: Theme): 'primary' | 'accent' | 'warn' {
    return this.isSubscribed(theme.id) ? 'warn' : 'primary';
  }
}