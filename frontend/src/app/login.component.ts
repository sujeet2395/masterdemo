import { Component } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  template: `
    <h2>Login</h2>
    <form (ngSubmit)="login($event)">
      <label>
        Username:
        <input [(ngModel)]="username" name="username" required />
      </label>
      <br />
      <label>
        Password:
        <input [(ngModel)]="password" name="password" type="password" required />
      </label>
      <br />
      <button type="submit">Login</button>
      <div *ngIf="error" style="color:red">{{error}}</div>
    </form>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  login(event: Event) {
    event.preventDefault();
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
                  this.router.navigate(['/tenants']);
      },
      error: () => {
        this.error = 'Invalid credentials';
      }
    });
  }
}
