import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/users';
  private apiUrl1 = 'http://localhost:8080/api/v1/auth';

  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());
  constructor(private http: HttpClient) {}

  hasToken(): boolean {
    return !!localStorage.getItem('loggedIn');
  }

  register(user: any): Observable<any> {
    // Використовуємо наявний ендпоінт для створення користувача
    return this.http.post<any>(`${this.apiUrl}`, user)
      .pipe(
        tap(userResponse => {
          // Зберігаємо інформацію про користувача в localStorage
          localStorage.setItem('user', JSON.stringify(userResponse));

          // Після цього викликаємо метод для отримання токена
          this.loadToken({ username: user.username, password: user.password });
        })
      );
  }

  login(credentials: any): Observable<any> {
    // Спочатку отримуємо інформацію про користувача
    return this.http.post<any>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(user => {
          // Зберігаємо дані користувача в localStorage
          localStorage.setItem('user', JSON.stringify(user));

          // Після цього викликаємо метод для отримання токена
          this.loadToken(credentials);
        })
      );
  }

// Метод для отримання токена після успішного входу
  private loadToken(credentials: any): void {
    this.http.post<any>(`${this.apiUrl1}/authenticate`, credentials)
      .subscribe(response => {
        // Зберігаємо токен в localStorage
        localStorage.setItem('token', response.token);
        this.setLoggedInStatus(true);
        console.log('Token:', response.token);
      });
  }

  getUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
  getUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
  deleteAccount(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  isLoggedIn() {
    return this.loggedIn.asObservable();
  }

  setLoggedInStatus(status: boolean) {
    this.loggedIn.next(status);
    if (status === true) {
      localStorage.setItem('loggedIn', 'true');
    } else {
      localStorage.removeItem('loggedIn');
    }
  }

  logout(): void {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    this.setLoggedInStatus(false);
  }

  changeUserRole(userId: number, role: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/role`, { role: role });
  }
}



