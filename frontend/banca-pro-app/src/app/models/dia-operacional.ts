export type StatusDiaOperacional = 'ABERTO' | 'FECHADO';

export interface DiaOperacional {
  id?: number;
  data: string;
  status: StatusDiaOperacional;
  saldoInicial: number;
  saldoFinal: number;
  totalApostado: number;
  totalGreen: number;
  totalRed: number;
  totalCashout: number;
  lucroPrejuizo: number;
  roi: number;
  quantidadeApostas: number;
  quantidadePendentes: number;
  abertoEm?: string;
  fechadoEm?: string;
}

export interface AbrirDiaRequest {
  data?: string;
  saldoInicial: number;
}
