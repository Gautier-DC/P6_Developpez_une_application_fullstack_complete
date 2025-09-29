import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AuthService } from '../services/auth.service';

/**
 * JWT Authentication Interceptor
 * Automatically adds JWT token to HTTP requests
 * Handles 401 errors by clearing authentication
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Skip auth header for public endpoints (but NOT logout which needs the token)
    const publicEndpoints = ['/api/auth/login', '/api/auth/register'];
    const isPublicEndpoint = publicEndpoints.some((endpoint) =>
      req.url.includes(endpoint)
    );

    if (isPublicEndpoint) {
      return next
        .handle(req)
        .pipe(catchError((error) => this.handleError(error)));
    }

    // Add JWT token to authorized requests
    const token = this.authService.getToken();

    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });

      return next
        .handle(authReq)
        .pipe(catchError((error) => this.handleError(error)));
    }

    return next
      .handle(req)
      .pipe(catchError((error) => this.handleError(error)));
  }

  /**
   * Handle HTTP errors, particularly 401 Unauthorized
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error.status === 401) {
      // Skip logout handling for auth requests (login/register) and logout requests
      const isAuthRequest = error.url?.includes('/api/auth/login') || error.url?.includes('/api/auth/register');
      const isLogoutRequest = error.url?.includes('/api/auth/logout');

      if (!isAuthRequest && !isLogoutRequest) {
        // Only redirect for authenticated API calls, not for login errors
        this.authService.logoutLocal();
      }
    }

    return throwError(() => error);
  }
}
