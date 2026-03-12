import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CustomerService } from '../../core/services/customer.service';
import { RegisterRequest } from '../../core/models/auth.models';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly customerService = inject(CustomerService);
  private readonly router = inject(Router);

  // ── View toggle ──────────────────────────────────────────────────────────
  isRegisterView = signal(false);

  // ── Login state ──────────────────────────────────────────────────────────
  username = '';
  password = '';
  loginLoading = signal(false);
  loginError = signal('');

  // ── Register state ───────────────────────────────────────────────────────
  reg: RegisterRequest = {
    names: '',
    lastnames: '',
    birthday: '',
    documentType: 'CC',
    documentNumber: '',
    cellphone: '',
    email: '',
    username: '',
    passUser: '',
  };
  registerLoading = signal(false);
  registerError = signal('');
  registerSuccess = signal('');

  // ── Methods ───────────────────────────────────────────────────────────────
  showRegister(): void {
    this.isRegisterView.set(true);
    this.registerError.set('');
    this.registerSuccess.set('');
  }

  showLogin(): void {
    this.isRegisterView.set(false);
    this.loginError.set('');
  }

  onLogin(): void {
    if (!this.username || !this.password) return;
    this.loginLoading.set(true);
    this.loginError.set('');

    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.loginLoading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.loginLoading.set(false);
        this.loginError.set('Credenciales inválidas. Intente de nuevo.');
      },
    });
  }

  onRegister(): void {
    this.registerLoading.set(true);
    this.registerError.set('');
    this.registerSuccess.set('');

    this.customerService.register(this.reg).subscribe({
      next: () => {
        this.registerLoading.set(false);
        this.registerSuccess.set('¡Usuario registrado exitosamente! Ya puedes iniciar sesión.');
        this.reg = { names: '', lastnames: '', birthday: '', documentType: 'CC',
          documentNumber: '', cellphone: '', email: '', username: '', passUser: '' };
      },
      error: (err: unknown) => {
        this.registerLoading.set(false);
        const e = err as { error?: { message?: string } };
        this.registerError.set(e?.error?.message || 'Error al registrar el usuario.');
      },
    });
  }
}
