export interface BilheteItem {
  id?: number;
  descricao: string;
  mercado: string;
  odd: number;
}

export interface Bilhete {
  id: number;
  tipo: 'RAPIDA' | 'AGENDADA';
  status:
    | 'AGENDADA'
    | 'APOSTA_FEITA'
    | 'EM_ANDAMENTO'
    | 'FECHADA'
    | 'CANCELADA';
  resultado: 'PENDENTE' | 'GREEN' | 'RED' | 'CASHOUT';
  valorApostado: number;
  oddTotal: number;
  retornoPrevisto: number;
  lucroPrevisto: number;
  tipster?: string;
  linkTipster?: string;
  observacao?: string;
  createdAt?: string;
  itens: BilheteItem[];
}

export interface CriarBilheteRequest {
  casaId: number;
  tipo: 'RAPIDA' | 'AGENDADA';
  valorApostado: number;
  tipster?: string;
  linkTipster?: string;
  observacao?: string;
  itens: BilheteItem[];
}
