import { Injectable, inject, signal, computed } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ArticleService } from './article.service';
import { Article } from '../models/article.models';

/**
 * State management service for articles using Angular Signals
 * Provides caching, reactive state, and computed values
 */
@Injectable({
  providedIn: 'root',
})
export class ArticleStateService {
  private readonly articleService = inject(ArticleService);

  // Private signals for state management
  private readonly _articles = signal<Article[]>([]);
  private readonly _isLoading = signal(false);
  private readonly _lastFetchTime = signal<Date | null>(null);
  private readonly _selectedSortOrder = signal<'desc' | 'asc'>('desc');

  // Public readonly signals
  readonly isLoading = this._isLoading.asReadonly();
  readonly lastFetchTime = this._lastFetchTime.asReadonly();
  readonly sortOrder = this._selectedSortOrder.asReadonly();

  // Computed signals for derived state
  readonly articles = computed(() => {
    const articles = [...this._articles()];
    const order = this._selectedSortOrder();

    return articles.sort((a, b) => {
      const dateA = new Date(a.createdAt).getTime();
      const dateB = new Date(b.createdAt).getTime();
      return order === 'desc' ? dateB - dateA : dateA - dateB;
    });
  });

  readonly articlesCount = computed(() => this._articles().length);

  readonly hasArticles = computed(() => this._articles().length > 0);

  readonly isStale = computed(() => {
    const lastFetch = this._lastFetchTime();
    if (!lastFetch) return true;

    const fiveMinutesAgo = new Date(Date.now() - 5 * 60 * 1000);
    return lastFetch < fiveMinutesAgo;
  });

  // Methods
  loadArticles(force = false): Observable<Article[]> {
    // Skip loading if we have fresh data and force is false
    if (!force && this.hasArticles() && !this.isStale()) {
      return new Observable(observer => {
        observer.next(this.articles());
        observer.complete();
      });
    }

    this._isLoading.set(true);

    return this.articleService.getAllArticles().pipe(
      tap(articles => {
        this._articles.set(articles);
        this._lastFetchTime.set(new Date());
        this._isLoading.set(false);
      })
    );
  }

  setSortOrder(order: 'desc' | 'asc'): void {
    this._selectedSortOrder.set(order);
  }

  toggleSortOrder(): void {
    const newOrder = this._selectedSortOrder() === 'desc' ? 'asc' : 'desc';
    this._selectedSortOrder.set(newOrder);
  }

  addArticle(article: Article): void {
    const currentArticles = this._articles();
    this._articles.set([...currentArticles, article]);
  }

  updateArticle(updatedArticle: Article): void {
    const currentArticles = this._articles();
    const index = currentArticles.findIndex(a => a.id === updatedArticle.id);

    if (index !== -1) {
      const newArticles = [...currentArticles];
      newArticles[index] = updatedArticle;
      this._articles.set(newArticles);
    }
  }

  removeArticle(articleId: number): void {
    const currentArticles = this._articles();
    const filteredArticles = currentArticles.filter(a => a.id !== articleId);
    this._articles.set(filteredArticles);
  }

  getArticleById(id: number): Article | undefined {
    return this._articles().find(article => article.id === id);
  }

  clearCache(): void {
    this._articles.set([]);
    this._lastFetchTime.set(null);
  }

  forceReload(): Observable<Article[]> {
    return this.loadArticles(true);
  }
}