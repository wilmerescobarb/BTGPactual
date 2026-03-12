export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  username: string;
}

export interface CustomerDto {
  id: string;
  username: string;
  amount: number;
  names: string;
  lastnames: string;
}

export interface RegisterRequest {
  names: string;
  lastnames: string;
  birthday: string;
  documentType: string;
  documentNumber: string;
  cellphone: string;
  email: string;
  username: string;
  passUser: string;
}
