import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-back-button',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './back-button.component.html',
  styleUrls: ['./back-button.component.scss'],
})
export class BackButtonComponent {
  @Input() customRoute?: string;
  @Input() ariaLabel: string = 'Go back';
  @Output() backClick = new EventEmitter<void>();

  constructor(private location: Location, private router: Router) {}

  onBackClick(): void {
    // Emit the backClick event for parent components
    this.backClick.emit();

    // Navigation logic
    if (this.customRoute) {
      this.router.navigate([this.customRoute]);
    } else {
      this.location.back();
    }
  }
}
