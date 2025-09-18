import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
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

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Skip auth header for public endpoints (but NOT logout which needs the token)
    const publicEndpoints = ['/api/auth/login', '/api/auth/register'];
    const isPublicEndpoint = publicEndpoints.some(endpoint => req.url.includes(endpoint));
    
    if (isPublicEndpoint) {
      return next.handle(req).pipe(
        catchError(error => this.handleError(error))
      );
    }

    // Add JWT token to authorized requests
    const token = this.authService.getToken();
    
    if (token) {
      console.log('🔍 Interceptor - Adding token to request:', req.url);
      console.log('🔍 Interceptor - Token exists:', !!token);
      
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      
      return next.handle(authReq).pipe(
        catchError(error => this.handleError(error))
      );
    } else {
      console.log('🔍 Interceptor - No token for request:', req.url);
    }

    return next.handle(req).pipe(
      catchError(error => this.handleError(error))
    );
  }

  /**
   * Handle HTTP errors, particularly 401 Unauthorized
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error.status === 401) {
      // Skip logout handling if this is already a logout request to avoid infinite loop
      const isLogoutRequest = error.url?.includes('/api/auth/logout');
      
      if (!isLogoutRequest) {
        // Token is invalid, expired, or blacklisted - clear authentication locally
        console.log('🔓 401 Unauthorized - Token may be blacklisted or expired');
        this.authService.logoutLocal();
      } else {
        console.log('🔓 401 on logout request - proceeding with local cleanup');
      }
    }
    
    return throwError(() => error);
  }
}