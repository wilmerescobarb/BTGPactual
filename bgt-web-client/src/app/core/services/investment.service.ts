import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  ApiResponse,
  CustomerInvestmentRequest,
  CustomerInvestmentResponse,
  InvestmentDto,
  InvestmentSummaryDto,
} from '../models/investment.models';

@Injectable({ providedIn: 'root' })
export class InvestmentService {
  private readonly http = inject(HttpClient);
  private readonly INVESTMENT_API = '/investments';

  /** GET /investments/catalog — lista de productos disponibles */
  getCatalog(): Observable<InvestmentDto[]> {
    return this.http
      .get<ApiResponse<InvestmentDto[]>>(`${this.INVESTMENT_API}/catalog`)
      .pipe(map((res) => res.data));
  }

  /** GET /investments — historial de inversiones del cliente autenticado */
  getHistory(): Observable<InvestmentSummaryDto[]> {
    return this.http
      .get<ApiResponse<InvestmentSummaryDto[]>>(this.INVESTMENT_API)
      .pipe(map((res) => res.data));
  }

  /** POST /investments/subscribe */
  subscribe(request: CustomerInvestmentRequest): Observable<ApiResponse<CustomerInvestmentResponse>> {
    return this.http.post<ApiResponse<CustomerInvestmentResponse>>(
      `${this.INVESTMENT_API}/subscribe`,
      request
    );
  }

  /** PUT /investments/unsubscribe/:id */
  unsubscribe(idCustomerInvestment: string): Observable<ApiResponse<CustomerInvestmentResponse>> {
    return this.http.put<ApiResponse<CustomerInvestmentResponse>>(
      `${this.INVESTMENT_API}/unsubscribe/${idCustomerInvestment}`,
      {}
    );
  }
}
