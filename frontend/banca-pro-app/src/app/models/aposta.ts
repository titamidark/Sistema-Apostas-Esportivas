export type TipoAposta = 'AGENDADA' | 'RAPIDA';

export type StatusAposta =
  | 'AGENDADA'
  | 'APOSTA_FEITA'
  | 'EM_ANDAMENTO'
  | 'FECHADA'
  | 'CANCELADA';

export type ResultadoAposta = 'PENDENTE' | 'GREEN' | 'RED' | 'CASHOUT';

export type OrigemAposta = 'TIPSTER' | 'ROBO';

export interface Aposta {
  id: number;
  casaId: number;

  origem?: OrigemAposta;

  tipster?: string;

  roboId?: number;
  metodoRoboId?: number;

  tipo: TipoAposta;
  status: StatusAposta;
  resultado: ResultadoAposta;

  mercado: string;
  palpite?: string;

  valorApostado: number;
  oddTipster: number;
  oddFeita: number;

  retornoPrevisto: number;
  possivelLucro: number;
  retornoReal: number;
  lucroPrejuizo: number;

  linkPrint?: string;
  dataJogo?: string;
  createdAt?: string;
  updatedAt?: string;
}
