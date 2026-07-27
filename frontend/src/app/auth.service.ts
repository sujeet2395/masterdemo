import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ConfigService } from './config.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'auth_token';
  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());

  constructor(
    private http: HttpClient, 
    private router: Router,
    private configService: ConfigService
  ) {
    // Log configuration on service initialization
    this.configService.logConfig();
  }

  login(username: string, password: string): Observable<any> {
    const loginUrl = this.configService.getApiUrl('auth/login');
    console.log('Login request to:', loginUrl);
    
    return this.http.post<any>(loginUrl, { username, password }).pipe(
      tap(response => {
        if (response && response.token) {
          localStorage.setItem(this.tokenKey, response.token);
          this.loggedIn.next(true);
        }
      })
    );
  }

  logout(): void {
    localStorage.clear();
    this.loggedIn.next(false);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getTokenPayload(): any | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    try {
      const payload = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(payload);
    } catch {
      return null;
    }
  }

  getUsername(): string | null {
    const payload = this.getTokenPayload();
    return payload?.sub || payload?.username || null;
  }

  getUserRole(): string | null {
    const payload = this.getTokenPayload();
    if (!payload || !payload.role) {
      return null;
    }
    return payload.role.startsWith('ROLE_') ? payload.role.replace('ROLE_', '') : payload.role;
  }

  hasAnyRole(roles: string[]): boolean {
    const role = this.getUserRole();
    if (!role || !roles?.length) {
      return false;
    }
    return roles.some(allowed => {
      const normalized = allowed.startsWith('ROLE_') ? allowed.replace('ROLE_', '') : allowed;
      return role === normalized;
    });
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }
}
