import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';

export interface UserAccount {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface UserFormPayload {
  username?: string;
  email: string;
  password?: string;
  role?: string;
  tenantId?: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient, private configService: ConfigService) {}

  getUsersByTenant(tenantId: number): Observable<UserAccount[]> {
    return this.http.get<UserAccount[]>(`${this.configService.getApiUrl('users')}/tenant/${tenantId}`);
  }

  createUserAsSuperAdmin(payload: UserFormPayload): Observable<string> {
    return this.http.post(`${this.configService.getApiUrl('users')}/register-admin`, payload, {
      responseType: 'text'
    });
  }

  createUserAsAdmin(payload: UserFormPayload, tenantId: number): Observable<string> {
    const params = new HttpParams().set('tenantId', tenantId.toString());
    return this.http.post(`${this.configService.getApiUrl('users')}/register-user`, payload, {
      params,
      responseType: 'text'
    });
  }

  updateUser(userId: number, payload: UserFormPayload): Observable<string> {
    return this.http.put(`${this.configService.getApiUrl('users')}/${userId}`, payload, {
      responseType: 'text'
    });
  }
}
