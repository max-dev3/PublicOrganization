import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';

import { Router } from '@angular/router';
import {AuthService} from "../services/auth.service";


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  submitted = false;
  errorMessage: string = "";

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
        username: ['', [Validators.required, Validators.minLength(6)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6), Validators.pattern('^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$')]],
        confirmPassword: ['', Validators.required],
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        phoneNumber: ['', [Validators.pattern('^((\\+38)[\\-]?)+[0-9]{10}$')]]
      },{
      validator: this.mustMatch('password', 'confirmPassword')
      });
  }

  ngOnInit() {

  }

  get f() { return this.registerForm.controls; }

  onSubmit() {
    this.submitted = true;
    if (this.registerForm.invalid) {
      return;
    }

    this.authService.register(this.registerForm.value)
      .subscribe(
        response => {
          console.log('Registration successful', response);
          this.router.navigate(['/login']);
        },
        error => {
          console.log('Registration failed', error);
          this.handleError({error: error});
        }
      );
  }
  handleError({error}: { error: any }) {
    const errorMessage = error.error.message;
    if (errorMessage.startsWith('Username ')) {
      this.errorMessage = 'Користувач з таким ім\'ям вже існує';
    } else if (errorMessage.startsWith('Phone number')) {
      this.errorMessage = 'Номер телефону вже використовується';
    } else if (errorMessage.startsWith('Email')) {
      this.errorMessage = 'Email вже використовується';
    } else {
      this.errorMessage = 'Сталася помилка під час реєстрації';
    }
  }

  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors['mustMatch']) {
        return;
      }

      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    }
  }
  onReset() {
    this.submitted = false;
    this.registerForm.reset();
    this.errorMessage = '';
  }
}
