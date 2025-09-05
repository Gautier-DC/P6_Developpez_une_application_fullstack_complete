/**
 * Authentication-related interfaces and types
 * Matches the backend DTOs for consistency
 */

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  email: string;
  expiresIn: number; // in seconds
}

export interface UserResponse {
  id: number;
  email: string;
  username: string;
  createdAt: string;
  updatedAt: string;
}

export interface ErrorResponse {
  error: string;
  message: string;
}

// Local storage keys
export const AUTH_STORAGE_KEYS = {
  TOKEN: 'auth_token',
  USER: 'current_user'
} as const;