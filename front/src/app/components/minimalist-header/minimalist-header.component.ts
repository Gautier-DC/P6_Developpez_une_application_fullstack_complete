import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-minimalist-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './minimalist-header.component.html',
  styleUrl: './minimalist-header.component.scss',
})
export class MinimalistHeaderComponent {}
