import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card',
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './card.component.html',
  styleUrl: './card.component.scss'
})
export class CardComponent {
  @Input() title!: string;
  @Input() author?: string;
  @Input() date?: string;
  @Input() description!: string;
  @Input() buttonText?: string;
  @Input() buttonColor: 'primary' | 'accent' | 'warn' = 'primary';
  @Input() buttonDisabled?: boolean = false;

  @Output() buttonClick = new EventEmitter<void>();
  @Output() cardClick = new EventEmitter<void>();

  onButtonClick(): void {
    this.buttonClick.emit();
  }

  onCardClick(): void {
    this.cardClick.emit();
  }
}
