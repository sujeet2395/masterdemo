import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserAccount, UserService, UserFormPayload } from '../user.service';
import { AuthService } from '../auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-tenant-user-management',
  templateUrl: './tenant-user-management.component.html',
  styleUrls: ['./tenant-user-management.component.css']
})
export class TenantUserManagementComponent implements OnInit {
  tenantId!: number;
  users: UserAccount[] = [];
  selectedUser: UserAccount | null = null;
  showForm = false;
  isSaving = false;
  error = '';
  successMessage = '';

  userForm: UserFormPayload = {
    username: '',
    email: '',
    password: '',
    role: 'USER'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const tenantIdParam = this.route.snapshot.paramMap.get('tenantId');
    this.tenantId = tenantIdParam ? Number(tenantIdParam) : 0;
    if (!this.tenantId) {
      this.router.navigate(['/tenants']);
      return;
    }
    this.loadUsers();
  }

  loadUsers(): void {
    this.error = '';
    this.successMessage = '';
    this.userService.getUsersByTenant(this.tenantId).subscribe({
      next: users => {
        this.users = users;
      },
      error: err => {
        this.handleError(err);
      }
    });
  }

  openNewUserForm(): void {
    this.selectedUser = null;
    this.showForm = true;
    this.error = '';
    this.successMessage = '';
    this.userForm = {
      username: '',
      email: '',
      password: '',
      role: 'USER'
    };
  }

  openEditForm(user: UserAccount): void {
    this.selectedUser = user;
    this.showForm = true;
    this.error = '';
    this.successMessage = '';
    this.userForm = {
      username: user.username,
      email: user.email,
      role: user.role,
      tenantId: this.tenantId
    };
  }

  closeForm(): void {
    this.showForm = false;
    this.error = '';
    this.successMessage = '';
    this.selectedUser = null;
  }

  saveUser(): void {
    if (!this.userForm.email || !this.userForm.email.trim()) {
      this.error = 'Email is required.';
      return;
    }

    if (!this.userForm.username || !this.userForm.username.trim()) {
      this.error = 'Username is required.';
      return;
    }

    this.isSaving = true;
    this.error = '';

    if (this.selectedUser) {
      const payload: UserFormPayload = {
        email: this.userForm.email.trim(),
        role: this.userForm.role
      };
      this.userService.updateUser(this.selectedUser.id, payload).subscribe({
        next: () => {
          this.isSaving = false;
          this.successMessage = 'User updated successfully.';
          this.loadUsers();
          this.closeForm();
        },
        error: err => {
          this.isSaving = false;
          this.handleError(err);
        }
      });
      return;
    }

    if (!this.userForm.password || !this.userForm.password.trim()) {
      this.error = 'Password is required for new users.';
      this.isSaving = false;
      return;
    }

    const payload: UserFormPayload = {
      username: this.userForm.username?.trim(),
      email: this.userForm.email.trim(),
      password: this.userForm.password.trim(),
      role: this.userForm.role,
      tenantId: this.tenantId
    };

    const userRole = this.authService.getUserRole();
    const isSuperAdmin = userRole === 'SUPER_ADMIN';

    const createRequest = isSuperAdmin
      ? this.userService.createUserAsSuperAdmin(payload)
      : this.userService.createUserAsAdmin(payload, this.tenantId);

    createRequest.subscribe({
      next: () => {
        this.isSaving = false;
        this.successMessage = 'User created successfully.';
        this.loadUsers();
        this.closeForm();
      },
      error: err => {
        this.isSaving = false;
        this.handleError(err);
      }
    });
  }

  private handleError(error: HttpErrorResponse): void {
    if (error?.error) {
      if (typeof error.error === 'string') {
        this.error = error.error;
        return;
      }
      if (error.error?.message) {
        this.error = error.error.message;
        return;
      }
      this.error = JSON.stringify(error.error);
      return;
    }

    this.error = error.statusText || 'Unable to complete the request.';
  }
}
