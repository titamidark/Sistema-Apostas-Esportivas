export type StatusCofrinhoDia = 'PENDENTE' | 'GREEN' | 'RED';

export type TipoCofrinho =
  | 'META_FIXA'
  | 'VALOR_VARIAVEL';

export interface Cofrinho {
  id: number;
  nome: string;
  descricao: string;

  valorTotal: number;
  valorOriginal?: number;
  valorReservadoInicial?: number;

  dias: number;
  dataInicio: string;
  dataFinal: string;

  tipo?: TipoCofrinho;

  createdAt?: string;
  updatedAt?: string;
}

export interface CofrinhoDia {
  id: number;
  cofrinhoId: number;
  dia: number;
  data: string;
  status: StatusCofrinhoDia;
  valorPrevisto: number;
  valorDepositado: number;
}

export interface CofrinhoResumo {
  reservaInicial: number;
  depositadoPeriodo: number;
  depositado: number;
  falta: number;
  pendentes: number;
  verdes: number;
  vermelhos: number;
  progresso: number;
  valorDiarioAtual: number;
}

export interface CofrinhoRequest {
  nome: string;
  descricao: string;
  valorTotal: number;
  valorReservadoInicial?: number;
  dataInicio: string;
  dias: number;
  tipo?: TipoCofrinho;
}