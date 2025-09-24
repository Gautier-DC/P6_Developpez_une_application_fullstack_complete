import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Article, CreateArticleRequest, Comment, CreateCommentRequest } from '../models/article.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Articles
  getAllArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.apiUrl}/articles`, {
      headers: this.getHeaders()
    });
  }

  getArticleById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.apiUrl}/articles/${id}`, {
      headers: this.getHeaders()
    });
  }

  getMyArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.apiUrl}/articles/my-articles`, {
      headers: this.getHeaders()
    });
  }

  getArticlesByTheme(themeId: number): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.apiUrl}/articles/by-theme/${themeId}`, {
      headers: this.getHeaders()
    });
  }

  searchArticles(keyword: string): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.apiUrl}/articles/search`, {
      headers: this.getHeaders(),
      params: { keyword }
    });
  }

  createArticle(article: CreateArticleRequest): Observable<Article> {
    return this.http.post<Article>(`${this.apiUrl}/articles`, article, {
      headers: this.getHeaders()
    });
  }

  updateArticle(id: number, article: CreateArticleRequest): Observable<Article> {
    return this.http.put<Article>(`${this.apiUrl}/articles/${id}`, article, {
      headers: this.getHeaders()
    });
  }

  deleteArticle(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/articles/${id}`, {
      headers: this.getHeaders()
    });
  }

  // Comments
  getCommentsByArticle(articleId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/comments/article/${articleId}`, {
      headers: this.getHeaders()
    });
  }

  createComment(comment: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/comments`, comment, {
      headers: this.getHeaders()
    });
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/comments/${id}`, {
      headers: this.getHeaders()
    });
  }

  getMyComments(): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/comments/my-comments`, {
      headers: this.getHeaders()
    });
  }
}