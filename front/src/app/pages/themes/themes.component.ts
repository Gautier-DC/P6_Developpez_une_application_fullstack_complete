import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ThemeService } from '../../services/theme.service';
import { Theme } from '../../models/article.models';
import { CardComponent } from '../../components/card/card.component';

@Component({
  selector: 'app-themes',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, CardComponent],
  templateUrl: './themes.component.html',
  styleUrls: ['./themes.component.scss'],
})
export class ThemesComponent implements OnInit {
  private themeService = inject(ThemeService);

  // Signals for reactive state management
  private readonly _themes = signal<Theme[]>([]);
  private readonly _subscribedThemes = signal(new Set<number>());
  private readonly _isLoading = signal(true);
  private readonly _processingThemes = signal(new Set<number>());

  // Public readonly signals
  readonly themes = this._themes.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();

  // Computed signals for derived state
  readonly subscribedThemes = computed(() => this._subscribedThemes());
  readonly processingThemes = computed(() => this._processingThemes());

  ngOnInit(): void {
    this.loadThemes();
    this.loadUserSubscriptions();
  }

  loadThemes(): void {
    this._isLoading.set(true);
    this.themeService.getAllThemes().subscribe({
      next: (themes) => {
        this._themes.set(themes);
        this._isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading themes:', error);
        this._isLoading.set(false);
      },
    });
  }

  loadUserSubscriptions(): void {
    this.themeService.getUserSubscriptions().subscribe({
      next: (subscriptions) => {
        this._subscribedThemes.set(new Set(subscriptions));
      },
      error: (error) => {
        console.error('Error loading user subscriptions:', error);
      },
    });
  }

  onSubscribeToggle(theme: Theme): void {
    if (this._processingThemes().has(theme.id)) {
      return;
    }

    // Add to processing set
    const newProcessing = new Set(this._processingThemes());
    newProcessing.add(theme.id);
    this._processingThemes.set(newProcessing);

    const isSubscribed = this._subscribedThemes().has(theme.id);

    if (isSubscribed) {
      this.unsubscribeFromTheme(theme);
    } else {
      this.subscribeToTheme(theme);
    }
  }

  private subscribeToTheme(theme: Theme): void {
    this.themeService.subscribeToTheme(theme.id).subscribe({
      next: () => {
        // Add to subscribed themes
        const newSubscribed = new Set(this._subscribedThemes());
        newSubscribed.add(theme.id);
        this._subscribedThemes.set(newSubscribed);

        // Remove from processing
        const newProcessing = new Set(this._processingThemes());
        newProcessing.delete(theme.id);
        this._processingThemes.set(newProcessing);
      },
      error: (error) => {
        console.error('Error subscribing to theme:', error);
        // Remove from processing on error
        const newProcessing = new Set(this._processingThemes());
        newProcessing.delete(theme.id);
        this._processingThemes.set(newProcessing);
      },
    });
  }

  private unsubscribeFromTheme(theme: Theme): void {
    this.themeService.unsubscribeFromTheme(theme.id).subscribe({
      next: () => {
        // Remove from subscribed themes
        const newSubscribed = new Set(this._subscribedThemes());
        newSubscribed.delete(theme.id);
        this._subscribedThemes.set(newSubscribed);

        // Remove from processing
        const newProcessing = new Set(this._processingThemes());
        newProcessing.delete(theme.id);
        this._processingThemes.set(newProcessing);
      },
      error: (error) => {
        console.error('Error unsubscribing from theme:', error);
        // Remove from processing on error
        const newProcessing = new Set(this._processingThemes());
        newProcessing.delete(theme.id);
        this._processingThemes.set(newProcessing);
      },
    });
  }

  // Utility methods for template
  isSubscribed(themeId: number): boolean {
    return this._subscribedThemes().has(themeId);
  }

  isProcessing(themeId: number): boolean {
    return this._processingThemes().has(themeId);
  }

  getButtonText(theme: Theme): string {
    const isProcessing = this._processingThemes().has(theme.id);
    const isSubscribed = this._subscribedThemes().has(theme.id);

    if (isProcessing) {
      return isSubscribed ? 'Désabonnement...' : 'Abonnement...';
    }
    return isSubscribed ? 'Déjà abonné' : "S'abonner";
  }

  getButtonColor(theme: Theme): 'primary' | 'accent' | 'warn' {
    const isSubscribed = this._subscribedThemes().has(theme.id);
    return isSubscribed ? 'warn' : 'primary';
  }
}
