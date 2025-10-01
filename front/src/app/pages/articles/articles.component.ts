import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { ArticleStateService } from '../../services/article-state.service';
import { DateService } from '../../services/date.service';
import { Article } from '../../models/article.models';
import { CardComponent } from '../../components/card/card.component';

@Component({
  selector: 'app-articles',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    CardComponent,
  ],
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss'],
})
export class ArticlesComponent implements OnInit {
  private articleStateService = inject(ArticleStateService);
  private dateService = inject(DateService);
  private router = inject(Router);

  // Expose state service signals
  readonly articles = this.articleStateService.articles;
  readonly isLoading = this.articleStateService.isLoading;
  readonly sortOrder = this.articleStateService.sortOrder;
  readonly articlesCount = this.articleStateService.articlesCount;
  readonly hasArticles = this.articleStateService.hasArticles;

  ngOnInit(): void {
    this.loadArticles();
  }

  loadArticles(): void {
    // Use loadArticles with force=false for normal caching behavior
    // But if there are no articles in cache, it will load them anyway
    this.articleStateService.loadArticles(false).subscribe({
      error: (error) => {
        console.error('Error loading articles:', error);
      },
    });
  }

  toggleSort(): void {
    this.articleStateService.toggleSortOrder();
  }

  onCreateArticle(): void {
    this.router.navigate(['/create-article']);
  }

  onArticleClick(article: Article): void {
    this.router.navigate(['/articles', article.id]);
  }

  formatDate(dateString: string): string {
    return this.dateService.formatDateTime(dateString);
  }

  formatRelativeDate(dateString: string): string {
    return this.dateService.getRelativeTime(dateString);
  }
}
