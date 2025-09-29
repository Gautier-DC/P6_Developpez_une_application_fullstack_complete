import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError, map, finalize } from 'rxjs/operators';

import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  UserResponse,
  UpdateProfileRequest,
  ErrorResponse,
  AUTH_STORAGE_KEYS,
} from '../models/auth.models';

/**
 * Authentication Service
 * Handles user authentication, token management, and user state
 * Uses Angular Signals for reactive state management
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';

  // Signals for reactive state management
  private readonly _currentUser = signal<UserResponse | null>(null);
  private readonly _isAuthenticated = signal<boolean>(false);
  private readonly _token = signal<string | null>(null);
  private readonly _isLoggingOut = signal<boolean>(false);

  // Computed signals (derived state)
  public readonly currentUser = this._currentUser.asReadonly();
  public readonly isAuthenticated = this._isAuthenticated.asReadonly();
  public readonly token = this._token.asReadonly();
  public readonly isLoggingOut = this._isLoggingOut.asReadonly();
  public readonly isLoggedIn = computed(
    () => this._isAuthenticated() && this._token() !== null
  );

  // Traditional BehaviorSubject for compatibility with guards and interceptors
  private readonly _authStatus = new BehaviorSubject<boolean>(false);
  public readonly authStatus$ = this._authStatus.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.initializeAuth();
  }

  /**
   * Initialize authentication state from local storage
   * Called on service creation (app startup)
   */
  private initializeAuth(): void {
    const token = this.getStoredToken();
    const user = this.getStoredUser();

    if (token && user && this.isTokenValid(token)) {
      this._token.set(token);
      this._currentUser.set(user);
      this._isAuthenticated.set(true);
      this._authStatus.next(true);
    } else {
      this.clearAuth();
    }
  }

  /**
   * User login
   * @param credentials - Login request with email and password
   * @returns Observable<AuthResponse>
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap((response) => {
          this.setAuthData(response);
          // Redirect to articles after successful login
          this.router.navigate(['/articles']);
        }),
        catchError((error) => this.handleAuthError('Login failed', error))
      );
  }

  /**
   * User registration
   * @param userData - Registration request with email, username, password
   * @returns Observable<AuthResponse>
   */
  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.API_URL}/register`, userData)
      .pipe(
        tap((response) => {
          this.setAuthData(response);
          // Redirect to articles after successful registration
          this.router.navigate(['/articles']);
        }),
        catchError((error) =>
          this.handleAuthError('Registration failed', error)
        )
      );
  }

  /**
   * Get current user profile
   * @returns Observable<UserResponse>
   */
  getCurrentUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/me`).pipe(
      tap((user) => this._currentUser.set(user)),
      catchError((error) =>
        this.handleAuthError('Failed to get user profile', error)
      )
    );
  }

  /**
   * Update user profile
   * @param profileData - Profile update request with username, email, and/or password
   * @returns Observable<UserResponse>
   */
  updateProfile(profileData: UpdateProfileRequest): Observable<UserResponse> {
    // Manually add Authorization header as fallback (like logout)
    const headers = {
      Authorization: `Bearer ${this._token()}`,
      'Content-Type': 'application/json',
    };

    return this.http
      .put<UserResponse>(`${this.API_URL}/update-profile`, profileData, {
        headers,
      })
      .pipe(
        tap((updatedUser) => {
          // Update current user in state
          this._currentUser.set(updatedUser);
          // Update stored user data
          localStorage.setItem(
            AUTH_STORAGE_KEYS.USER,
            JSON.stringify(updatedUser)
          );
        }),
        catchError((error) =>
          this.handleAuthError('Failed to update profile', error)
        )
      );
  }

  /**
   * Logout user
   * Calls backend to blacklist token, then clears local auth data
   */
  logout(): Observable<string> {
    // If no token, just clear local data
    if (!this._token()) {
      this.clearAuth();
      this.router.navigate(['/auth/login']);
      return new Observable((observer) => {
        observer.next('Already logged out');
        observer.complete();
      });
    }

    this._isLoggingOut.set(true);
    // Manually add Authorization header as fallback
    const headers = {
      Authorization: `Bearer ${this._token()}`,
      'Content-Type': 'application/json',
    };

    return this.http
      .post(
        `${this.API_URL}/logout`,
        {},
        {
          headers,
          responseType: 'text',
        }
      )
      .pipe(
        tap(() => {
          this.clearAuth();
          this.router.navigate(['/']);
        }),
        catchError((error) => {
          console.error('❌ Server logout failed:', error);

          // Even if server logout fails, clear local data
          this.clearAuth();
          this.router.navigate(['/']); // Redirect to home page
          return throwError(() => new Error('Logout completed locally'));
        }),
        finalize(() => this._isLoggingOut.set(false))
      );
  }

  /**
   * Quick logout (client-side only)
   * For cases where you want immediate logout without server call
   */
  logoutLocal(): void {
    this.clearAuth();
    this.router.navigate(['/']); // Redirect to home page
  }

  /**
   * Check if user is authenticated
   * @returns boolean
   */
  isUserAuthenticated(): boolean {
    return this._isAuthenticated() && this._token() !== null;
  }

  /**
   * Get current JWT token
   * @returns string | null
   */
  getToken(): string | null {
    return this._token();
  }

  /**
   * Get current user
   * @returns UserResponse | null
   */
  getUser(): UserResponse | null {
    return this._currentUser();
  }

  /**
   * Set authentication data after successful login/register
   * @param response - Auth response from backend
   */
  private setAuthData(response: AuthResponse): void {
    // Store token
    this._token.set(response.token);
    localStorage.setItem(AUTH_STORAGE_KEYS.TOKEN, response.token);

    // Create user object from auth response
    const user: UserResponse = {
      id: 0, // Will be updated when we fetch full profile
      email: response.email,
      username: response.username,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    // Store user
    this._currentUser.set(user);
    localStorage.setItem(AUTH_STORAGE_KEYS.USER, JSON.stringify(user));

    // Update authentication status
    this._isAuthenticated.set(true);
    this._authStatus.next(true);
  }

  /**
   * Clear all authentication data
   */
  private clearAuth(): void {
    this._token.set(null);
    this._currentUser.set(null);
    this._isAuthenticated.set(false);
    this._isLoggingOut.set(false);
    this._authStatus.next(false);

    localStorage.removeItem(AUTH_STORAGE_KEYS.TOKEN);
    localStorage.removeItem(AUTH_STORAGE_KEYS.USER);
  }

  /**
   * Get stored token from localStorage
   */
  private getStoredToken(): string | null {
    return localStorage.getItem(AUTH_STORAGE_KEYS.TOKEN);
  }

  /**
   * Get stored user from localStorage
   */
  private getStoredUser(): UserResponse | null {
    const userData = localStorage.getItem(AUTH_STORAGE_KEYS.USER);
    if (userData) {
      try {
        return JSON.parse(userData);
      } catch (error) {
        console.error('Failed to parse stored user data:', error);
        return null;
      }
    }
    return null;
  }

  /**
   * Basic token validation (checks if token exists and is not expired)
   * @param token - JWT token
   * @returns boolean
   */
  private isTokenValid(token: string): boolean {
    if (!token) return false;

    try {
      // Simple JWT expiration check
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch (error) {
      console.error('Token validation failed:', error);
      return false;
    }
  }

  /**
   * Handle authentication errors with better user messaging
   * @param message - Default error message
   * @param error - HTTP error response
   */
  private handleAuthError(
    message: string,
    error: HttpErrorResponse
  ): Observable<never> {
    let errorMessage: string;

    // Check if server provided a specific error message
    if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else {
      // Fallback based on status code
      switch (error.status) {
        case 401:
          errorMessage = 'Email ou mot de passe invalide';
          break;
        case 500:
          errorMessage = 'Impossible de se connecter au serveur';
          break;
        case 504:
          errorMessage = 'Une erreur est survenue lors de la connexion';
          break;
        default:
          errorMessage = message;
      }
    }

    console.error('❌ Auth Error:', { status: error.status, message: errorMessage }, error);
    return throwError(() => new Error(errorMessage));
  }
}
