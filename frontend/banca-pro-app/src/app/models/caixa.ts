export type TipoMovimentacaoCaixa =
  | 'SALDO_INICIAL_DIA'
  | 'ENTRADA_MANUAL'
  | 'SAIDA_MANUAL'
  | 'APOSTA'
  | 'DEPOSITO_CASA'
  | 'SAQUE_CASA'
  | 'AJUSTE';

export interface MovimentacaoCaixa {
  id: number;
  tipo: TipoMovimentacaoCaixa;
  valor: number;
  descricao?: string;
  dataMovimentacao: string;
  dataReferencia?: string;
}

export interface CaixaResumo {
  saldoInicial: number;
  adicionado: number;
  saidas: number;
  apostado: number;
  transferidoParaCasas: number;
  saqueDasCasas: number;
  ajustes: number;
  saldoAtual: number;
}
