import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { InvestmentService } from '../../core/services/investment.service';
import { InvestmentSummaryDto } from '../../core/models/investment.models';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe],
  templateUrl: './history.html',
  styleUrl: './history.scss',
})
export class HistoryComponent implements OnInit {
  private readonly investmentService = inject(InvestmentService);

  investments = signal<InvestmentSummaryDto[]>([]);
  loading = signal(true);
  error = signal('');

  // Unsubscribe state
  unsubscribingId = signal<string | null>(null);
  successMsg = signal('');
  errorMsg = signal('');

  // Confirmation modal
  showConfirmModal = signal(false);
  confirmTarget = signal<InvestmentSummaryDto | null>(null);

  // Pagination
  readonly pageSize = 5;
  currentPage = signal(1);

  totalPages = computed(() => Math.max(1, Math.ceil(this.investments().length / this.pageSize)));

  pagedInvestments = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize;
    return this.investments().slice(start, start + this.pageSize);
  });

  pages = computed(() =>
    Array.from({ length: this.totalPages() }, (_, i) => i + 1)
  );

  pageEnd = computed(() =>
    Math.min(this.currentPage() * this.pageSize, this.investments().length)
  );

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.loading.set(true);
    this.error.set('');
    this.successMsg.set('');
    this.errorMsg.set('');
    this.currentPage.set(1);

    this.investmentService.getHistory().subscribe({
      next: (data) => {
        this.investments.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error al cargar el historial de inversiones.');
        this.loading.set(false);
      },
    });
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
    }
  }

  openConfirm(investment: InvestmentSummaryDto): void {
    this.confirmTarget.set(investment);
    this.showConfirmModal.set(true);
    this.successMsg.set('');
    this.errorMsg.set('');
  }

  closeConfirm(): void {
    this.showConfirmModal.set(false);
    this.confirmTarget.set(null);
  }

  confirmUnsubscribe(): void {
    const target = this.confirmTarget();
    if (!target) return;

    this.unsubscribingId.set(target.idCustomerInvestment);
    this.closeConfirm();

    this.investmentService.unsubscribe(target.idCustomerInvestment).subscribe({
      next: (res) => {
        this.unsubscribingId.set(null);
        this.successMsg.set(res.message || '¡Suscripción cancelada exitosamente!');
        this.loadHistory();
      },
      error: (err: unknown) => {
        this.unsubscribingId.set(null);
        const e = err as { error?: { message?: string } };
        const msg = e?.error?.message || 'Error al cancelar la suscripción.';
        this.errorMsg.set(msg);
      },
    });
  }

  isActive(status: string): boolean {
    return status === 'A';
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      A: 'status-active',
      C: 'status-cancelled',
    };
    return map[status] ?? 'status-default';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      A: 'Activo',
      C: 'Cancelado',
    };
    return map[status] ?? status;
  }
}
