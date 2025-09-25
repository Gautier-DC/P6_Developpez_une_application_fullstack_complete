import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { passwordValidator } from '../validators/password.validator';

@Directive({
  selector: '[appPasswordValidation]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: PasswordValidationDirective,
      multi: true
    }
  ]
})
export class PasswordValidationDirective implements Validator {

  validate(control: AbstractControl): ValidationErrors | null {
    return passwordValidator()(control);
  }
}