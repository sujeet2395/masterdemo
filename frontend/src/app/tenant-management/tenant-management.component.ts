import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth.service';
import { ConfigService } from '../config.service';

interface Tenant {
  id?: number;
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
  filteredTenants: Tenant[] = [];
  error = '';
  isLoading = false;
  isSaving = false;
  searchTerm = '';
  totalTenants = 0;
  activeTenants = 0;
  lastUpdated: string | null = null;
  showForm = false;
  isEditing = false;
  editingTenantId: number | null = null;
  tenantDraft: Tenant = { name: '', domain: '' };

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private configService: ConfigService,
  ) { }

  ngOnInit(): void {
    this.loadTenants();
  }

  loadTenants(): void {
    this.isLoading = true;
    this.error = '';
    const tenantsUrl = this.configService.getApiUrl('tenants');

    this.http.get<Tenant[]>(tenantsUrl).subscribe({
      next: (data) => {
        this.tenants = data;
        this.filteredTenants = data;
        this.totalTenants = data.length;
        this.activeTenants = data.filter(tenant => tenant.domain).length;
        this.lastUpdated = new Date().toLocaleString();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load tenants:', error);
        this.isLoading = false;
        this.filteredTenants = [];
        this.error = 'Unable to reach the SaaS backend API. Please verify the backend service is running.';
      }
    });
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();

    if (!term) {
      this.filteredTenants = this.tenants;
      return;
    }

    this.filteredTenants = this.tenants.filter(tenant =>
      tenant.name.toLowerCase().includes(term) || tenant.domain.toLowerCase().includes(term)
    );
  }

  openCreateForm(): void {
    this.isEditing = false;
    this.editingTenantId = null;
    this.tenantDraft = { name: '', domain: '' };
    this.error = '';
    this.showForm = true;
  }

  openEditForm(tenant: Tenant): void {
    this.isEditing = true;
    this.editingTenantId = tenant.id ?? null;
    this.tenantDraft = {
      id: tenant.id,
      name: tenant.name,
      domain: tenant.domain
    };
    this.error = '';
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.isSaving = false;
    this.isEditing = false;
    this.editingTenantId = null;
    this.tenantDraft = { name: '', domain: '' };
  }

  saveTenant(): void {
    if (!this.tenantDraft.name.trim()) {
      this.error = 'Tenant name is required.';
      return;
    }

    this.isSaving = true;
    this.error = '';

    const payload = {
      name: this.tenantDraft.name.trim(),
      domain: this.tenantDraft.domain.trim()
    };

    const request = this.isEditing && this.editingTenantId != null
      ? this.http.put<Tenant>(`${this.configService.getApiUrl('tenants')}/${this.editingTenantId}`, payload)
      : this.http.post<Tenant>(this.configService.getApiUrl('tenants'), payload);

    request.subscribe({
      next: () => {
        this.isSaving = false;
        this.closeForm();
        this.loadTenants();
      },
      error: () => {
        this.isSaving = false;
        this.error = this.isEditing ? 'Unable to update tenant. Please try again.' : 'Unable to create tenant. Please try again.';
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}