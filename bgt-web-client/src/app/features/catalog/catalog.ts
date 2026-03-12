import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvestmentService } from '../../core/services/investment.service';
import { InvestmentDto, CustomerInvestmentRequest } from '../../core/models/investment.models';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  templateUrl: './catalog.html',
  styleUrl: './catalog.scss',
})
export class CatalogComponent implements OnInit {
  private readonly investmentService = inject(InvestmentService);

  investments = signal<InvestmentDto[]>([]);
  loading = signal(true);
  error = signal('');

  // Modal state
  showModal = signal(false);
  selectedInvestment = signal<InvestmentDto | null>(null);
  subscribeAmount = signal(0);
  subscribing = signal(false);
  subscribeSuccess = signal('');
  subscribeError = signal('');

  ngOnInit(): void {
    this.loadCatalog();
  }

  loadCatalog(): void {
    this.loading.set(true);
    this.error.set('');
    this.investmentService.getCatalog().subscribe({
      next: (data) => {
        this.investments.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error al cargar el catálogo de inversiones.');
        this.loading.set(false);
      },
    });
  }

  openSubscribeModal(investment: InvestmentDto): void {
    this.selectedInvestment.set(investment);
    this.subscribeAmount.set(investment.minAmount);
    this.subscribeSuccess.set('');
    this.subscribeError.set('');
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.selectedInvestment.set(null);
  }

  onSubscribe(): void {
    const inv = this.selectedInvestment();
    if (!inv) return;

    const amount = this.subscribeAmount();
    if (amount < inv.minAmount) {
      this.subscribeError.set(`El monto mínimo para este fondo es $${inv.minAmount.toLocaleString('es-CO')}`);
      return;
    }

    const request: CustomerInvestmentRequest = {
      investment: inv.id,
      amount: amount,
    };

    this.subscribing.set(true);
    this.subscribeError.set('');

    this.investmentService.subscribe(request).subscribe({
      next: (res) => {
        this.subscribing.set(false);
        this.subscribeSuccess.set(res.message || '¡Suscripción creada exitosamente!');
      },
      error: (err: unknown) => {
        this.subscribing.set(false);
        const e = err as { error?: { message?: string } };
        const msg = e?.error?.message || 'Error al procesar la suscripción.';
        this.subscribeError.set(msg);
      },
    });
  }

  getCategoryLabel(category: string): string {
    const labels: Record<string, string> = {
      FPV: 'Fondo de Prima Variable',
      FIC: 'Fondo de Inversión Colectiva',
    };
    return labels[category] ?? category;
  }
}
