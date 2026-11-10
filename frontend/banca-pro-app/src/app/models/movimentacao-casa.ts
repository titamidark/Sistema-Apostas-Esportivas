export type TipoMovimentacaoCasa =
  | 'SALDO_INICIAL'
  | 'DEPOSITO'
  | 'SAQUE'
  | 'APOSTA'
  | 'GREEN'
  | 'RED'
  | 'CASHOUT'
  | 'AJUSTE';

export interface MovimentacaoCasa {
  id: number;
  casaId: number;
  tipo: TipoMovimentacaoCasa;
  valor: number;
  descricao?: string;
  dataMovimentacao: string;
}
