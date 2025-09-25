import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/login-request';
import { BackButtonComponent } from "src/app/components/back-button/back-button.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    BackButtonComponent
],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  hidePassword = true;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Sucess message from registration
    this.route.queryParams.subscribe(params => {
      if (params['message']) {
        this.successMessage = params['message'];
      }
    });

    this.loginForm = this.fb.group({
      emailOrUsername: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const loginData: LoginRequest = this.loginForm.value;

      this.authService.login(loginData).subscribe({
        next: () => {
          this.isLoading = false;
          // Redirection vers la page demandée ou articles par défaut
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/articles';
          this.router.navigateByUrl(returnUrl);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Une erreur est survenue lors de la connexion';
        }
      });
    }
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}
