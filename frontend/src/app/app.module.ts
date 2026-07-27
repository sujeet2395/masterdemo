import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { TenantManagementComponent } from './tenant-management/tenant-management.component';
import { LoginComponent } from './login.component';
import { AuthGuard } from './auth.guard';
import { RoleGuard } from './role.guard';
import { AuthInterceptor } from './auth.interceptor';
import { TenantUserManagementComponent } from './tenant-user-management/tenant-user-management.component';
import { FormsModule } from '@angular/forms';

const routes: Routes = [
  { path: '', redirectTo: '/tenants', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'tenants', component: TenantManagementComponent, canActivate: [AuthGuard] },
  {
    path: 'tenants/:tenantId/users',
    component: TenantUserManagementComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['SUPER_ADMIN', 'ADMIN'] }
  }
];

@NgModule({
  declarations: [
    AppComponent,
    TenantManagementComponent,
    LoginComponent,
    TenantUserManagementComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    FormsModule
  ],
  providers: [
    AuthGuard,
    RoleGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }