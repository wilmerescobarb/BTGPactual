import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly AUTH_API = '/auth';
  private readonly TOKEN_KEY = 'bgt_token';
  private readonly USERNAME_KEY = 'bgt_username';

  private _token = signal<string | null>(this.loadToken());
  private _username = signal<string | null>(localStorage.getItem(this.USERNAME_KEY));

  readonly isAuthenticated = computed(() => !!this._token());
  readonly currentUsername = computed(() => this._username());
  readonly token = computed(() => this._token());

  private loadToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.AUTH_API}/login`, request).pipe(
      tap((response) => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        localStorage.setItem(this.USERNAME_KEY, response.username);
        this._token.set(response.token);
        this._username.set(response.username);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USERNAME_KEY);
    this._token.set(null);
    this._username.set(null);
    this.router.navigate(['/login']);
  }
}
