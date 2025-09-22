import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ArticleService } from '../../services/article.service';
import { ThemeService } from '../../services/theme.service';
import { Theme, CreateArticleRequest } from '../../models/article.models';
import { BackButtonComponent } from 'src/app/components/back-button/back-button.component';

@Component({
  selector: 'app-create-article',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatAutocompleteModule,
    MatTooltipModule,
    BackButtonComponent
  ],
  templateUrl: './create-article.component.html',
  styleUrl: './create-article.component.scss'
})
export class CreateArticleComponent implements OnInit {
  private articleService = inject(ArticleService);
  private themeService = inject(ThemeService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  themes = signal<Theme[]>([]);
  isLoading = signal(false);
  isSubmitting = signal(false);

  articleForm = signal({
    title: '',
    content: '',
    themeName: ''
  });

  filteredThemes = computed(() => {
    const filter = this.articleForm().themeName.toLowerCase();
    if (!filter) {
      return this.themes();
    }
    return this.themes().filter(theme =>
      theme.name.toLowerCase().includes(filter)
    );
  });

  ngOnInit(): void {
    this.loadThemes();
  }

  loadThemes(): void {
    this.isLoading.set(true);
    this.themeService.getAllThemes().subscribe({
      next: (themes) => {
        this.themes.set(themes);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading themes:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url
        });
        this.snackBar.open(`Erreur lors du chargement des thèmes (${error.status})`, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isLoading.set(false);
      }
    });
  }

  updateThemeName(value: string): void {
    this.articleForm.update(form => ({
      ...form,
      themeName: value
    }));
  }

  updateTitle(value: string): void {
    this.articleForm.update(form => ({
      ...form,
      title: value
    }));
  }

  updateContent(value: string): void {
    this.articleForm.update(form => ({
      ...form,
      content: value
    }));
  }

  onSubmit(): void {
    if (!this.isFormValid()) {
      this.snackBar.open('Veuillez remplir tous les champs', 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.isSubmitting.set(true);
    const form = this.articleForm();

    // Vérifier si le thème existe
    const existingTheme = this.themes().find(theme =>
      theme.name.toLowerCase() === form.themeName.toLowerCase()
    );

    if (existingTheme) {
      // Utiliser le thème existant
      this.createArticleWithTheme(existingTheme.id);
    } else {
      // Créer un nouveau thème puis l'article
      this.themeService.createTheme({
        name: form.themeName,
        description: `Thème créé automatiquement : ${form.themeName}`
      }).subscribe({
        next: (newTheme) => {
          this.themes.update(themes => [...themes, newTheme]);
          this.createArticleWithTheme(newTheme.id);
        },
        error: (error) => {
          console.error('Error creating theme:', error);
          this.snackBar.open('Erreur lors de la création du thème', 'Fermer', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.isSubmitting.set(false);
        }
      });
    }
  }

  private createArticleWithTheme(themeId: number): void {
    const form = this.articleForm();
    const createRequest: CreateArticleRequest = {
      title: form.title,
      content: form.content,
      themeId: themeId
    };

    this.articleService.createArticle(createRequest).subscribe({
      next: () => {
        this.snackBar.open('Article créé avec succès !', 'Fermer', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.router.navigate(['/articles']);
      },
      error: (error) => {
        console.error('Error creating article:', error);
        this.snackBar.open('Erreur lors de la création de l\'article', 'Fermer', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.isSubmitting.set(false);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/articles']);
  }

  isFormValid(): boolean {
    const form = this.articleForm();
    return !!(
      form.title.trim() &&
      form.content.trim() &&
      form.themeName.trim()
    );
  }

  onThemeSelected(theme: Theme): void {
    this.updateThemeName(theme.name);
  }
}
