import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth.service';
import { ConfigService } from '../config.service';

interface Tenant {
  id: number;
  name: string;
  domain: string;
}

@Component({
  selector: 'app-tenant-management',
  templateUrl: './tenant-management.component.html',
  styleUrls: ['./tenant-management.component.css']
})
export class TenantManagementComponent implements OnInit {
  tenants: Tenant[] = [];
  error: string = '';

  constructor(
    private http: HttpClient, 
    private authService: AuthService,
    private configService: ConfigService,
    
  ) { }

  ngOnInit(): void {
    this.loadTenants();
  }

  loadTenants(): void {
    const tenantsUrl = this.configService.getApiUrl('tenants');
    console.log('Loading tenants from:', tenantsUrl);
    
    this.http.get<Tenant[]>(tenantsUrl).subscribe({
      next: (data) => {
        this.tenants = data;
        console.log('✅ Tenants loaded:', data);
      },
      error: (error) => {
        console.error('❌ Failed to load tenants:', error);
        this.error = 'Failed to load tenants. Ensure backend is running on localhost:8080';
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}