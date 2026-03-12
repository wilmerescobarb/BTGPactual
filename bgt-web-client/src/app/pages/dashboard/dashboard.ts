import { Component, inject, signal, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CustomerService } from '../../core/services/customer.service';
import { CustomerDto } from '../../core/models/auth.models';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CurrencyPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent implements OnInit {
  readonly auth = inject(AuthService);
  readonly customerService = inject(CustomerService);
  private readonly destroyRef = inject(DestroyRef);

  menuOpen = signal(false);
  customer = signal<CustomerDto | null>(null);

  ngOnInit(): void {
    this.customerService.customer$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => this.customer.set(data),
      });
    // Fuerza una petición fresca al montar el componente,
    // garantizando que se carguen los datos del usuario actual.
    this.customerService.refreshCustomer();
  }

  get fullName(): string {
    const c = this.customer();
    return c ? `${c.names} ${c.lastnames}` : (this.auth.currentUsername() ?? '');
  }

  toggleMenu(): void {
    this.menuOpen.update((v) => !v);
  }

  logout(): void {
    this.auth.logout();
  }
}
