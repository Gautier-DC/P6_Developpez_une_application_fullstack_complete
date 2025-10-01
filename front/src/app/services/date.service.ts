import { Injectable } from '@angular/core';

/**
 * Service for date formatting and timezone handling
 * Converts UTC dates from backend to local timezone for display
 */
@Injectable({
  providedIn: 'root',
})
export class DateService {

  /**
   * Format a UTC date string to local timezone with French locale
   * @param dateString - UTC date string from backend
   * @returns Formatted date string in local timezone
   */
  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      // Browser automatically converts from UTC to local timezone
    });
  }

  /**
   * Format a UTC date string to local timezone (date only)
   * @param dateString - UTC date string from backend
   * @returns Formatted date string in local timezone
   */
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  /**
   * Format a UTC date string to local timezone (time only)
   * @param dateString - UTC date string from backend
   * @returns Formatted time string in local timezone
   */
  formatTime(dateString: string): string {
    return new Date(dateString).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  /**
   * Get relative time (e.g., "il y a 2 heures")
   * @param dateString - UTC date string from backend
   * @returns Relative time string
   */
  getRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (diffInSeconds < 60) {
      return 'à l\'instant';
    }

    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return `il y a ${diffInMinutes} minute${diffInMinutes > 1 ? 's' : ''}`;
    }

    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return `il y a ${diffInHours} heure${diffInHours > 1 ? 's' : ''}`;
    }

    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 30) {
      return `il y a ${diffInDays} jour${diffInDays > 1 ? 's' : ''}`;
    }

    // For older dates, return formatted date
    return this.formatDate(dateString);
  }
}