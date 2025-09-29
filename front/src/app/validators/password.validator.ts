import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.value;

    // If there is no password or space for update profil
    if (!password || !password.trim()) {
      return null;
    }

    const errors: ValidationErrors = {};

    const trimmedPassword = password.trim();

    // Minimum 8 characters
    if (trimmedPassword.length < 8) {
      errors['minLength'] = {
        requiredLength: 8,
        actualLength: trimmedPassword.length,
      };
    }

    // At least one digit
    if (!/\d/.test(trimmedPassword)) {
      errors['requiresDigit'] = true;
    }

    // At least one lowercase letter
    if (!/[a-z]/.test(trimmedPassword)) {
      errors['requiresLowercase'] = true;
    }

    // At least one uppercase letter
    if (!/[A-Z]/.test(trimmedPassword)) {
      errors['requiresUppercase'] = true;
    }

    // At least one special character
    if (!/[@#$%^&+=!?.,:;()\[\]{}|\-_~`]/.test(trimmedPassword)) {
      errors['requiresSpecialChar'] = true;
    }

    return Object.keys(errors).length > 0 ? errors : null;
  };
}

export function getPasswordErrorMessage(errors: ValidationErrors): string {
  if (errors['required']) {
    return 'Le mot de passe est obligatoire';
  }

  return 'Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial';
}
