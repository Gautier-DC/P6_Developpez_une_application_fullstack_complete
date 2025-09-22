import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Theme } from '../models/article.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getAllThemes(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${this.apiUrl}/themes`, {
      headers: this.getHeaders()
    });
  }

  getThemeById(id: number): Observable<Theme> {
    return this.http.get<Theme>(`${this.apiUrl}/themes/${id}`, {
      headers: this.getHeaders()
    });
  }

  createTheme(themeData: { name: string; description?: string }): Observable<Theme> {
    return this.http.post<Theme>(`${this.apiUrl}/themes`, themeData, {
      headers: this.getHeaders()
    });
  }
}