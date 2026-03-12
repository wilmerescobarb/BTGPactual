export interface InvestmentDto {
  id: string;
  name: string;
  minAmount: number;
  category: string;
}

export interface InvestmentSummaryDto {
  idCustomerInvestment: string;
  idInvestment: string;
  nameInvestment: string;
  openedAt: string;
  closedAt: string | null;
  investmentAmount: number;
  status: string;
}

export interface CustomerInvestmentRequest {
  investment: string;
  amount: number;
  notificationEmail: boolean;
  notificationSms: boolean;
}

export interface CustomerInvestmentResponse {
  idCustomerInvestment: string;
  idInvestment: string;
  nameInvestment: string;
  openedAt: string;
  closedAt: string | null;
  investmentAmount: number;
  status: string;
}

export interface ApiResponse<T> {
  message: string;
  data: T;
}
