import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoggedIn = false;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe(
        (response) => {
          this.router.navigate(['/']); // перенаправлення на головну сторінку
          this.authService.setLoggedInStatus(true);
        },
        (error) => {
          console.log('Login failed', error);
          const errorMessage = error.error.message;
          if (errorMessage.startsWith('User with ')) {
            this.errorMessage = 'Користувача з таким ім\'ям не знайдено';
          } else if (errorMessage.startsWith('Invalid password')) {
            this.errorMessage = 'Невірний пароль';
          } else {
            this.errorMessage = 'Помилка під час входу';
          }
        }
      );
    }
  }
}
