import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from '../models/investment.models';
import { CustomerDto, RegisterRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly http = inject(HttpClient);
  private readonly CUSTOMER_API = '/customer';

  private readonly refreshTrigger$ = new BehaviorSubject<void>(undefined);

  /**
   * Stream reactivo del cliente autenticado.
   * Se re-ejecuta cada vez que se llame refreshCustomer().
   * Sin caché para evitar datos de sesiones anteriores.
   */
  readonly customer$: Observable<CustomerDto> = this.refreshTrigger$.pipe(
    switchMap(() =>
      this.http
        .get<ApiResponse<CustomerDto>>(this.CUSTOMER_API)
        .pipe(map((res) => res.data))
    )
  );

  /** Fuerza una nueva consulta al API de customer */
  refreshCustomer(): void {
    this.refreshTrigger$.next();
  }

  /** POST /customer – registro de un nuevo cliente */
  register(request: RegisterRequest): Observable<CustomerDto> {
    return this.http
      .post<ApiResponse<CustomerDto>>(this.CUSTOMER_API, request)
      .pipe(map((res) => res.data));
  }
}
