import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { PageLayoutComponent } from 'src/app/components/page-layout/page-layout.component';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [MatButtonModule, PageLayoutComponent],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  title = 'Welcome to MDD';

  constructor(private router: Router) {}

  ngOnInit(): void {}

  login() {
    this.router.navigate(['/login']);
  }

  register() {
    this.router.navigate(['/register']);
  }
}
