import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private apiBaseUrl = environment.apiBaseUrl;
  private apiVersion = environment.apiVersion;

  getApiBaseUrl(): string {
    return this.apiBaseUrl;
  }

  getApiUrl(endpoint: string): string {
    return `${this.apiBaseUrl}/api/${endpoint}`;
  }

  getApiVersionUrl(endpoint: string): string {
    return `${this.apiBaseUrl}/api/${this.apiVersion}/${endpoint}`;
  }

  isProduction(): boolean {
    return environment.production;
  }

  logConfig(): void {
    console.log('API Configuration:', {
      baseUrl: this.apiBaseUrl,
      apiVersion: this.apiVersion,
      production: environment.production
    });
  }
}
