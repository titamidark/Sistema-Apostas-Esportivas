export type StatusProjetoAlavancagem = 'ATIVO' | 'PAUSADO' | 'ENCERRADO';

export type StatusCicloAlavancagem = 'ABERTO' | 'GREEN' | 'RED';

export interface ProjetoAlavancagem {
  id: number;
  nome: string;
  descricao?: string;

  valorInicial: number;
  stakeAtual: number;
  proximaAposta: number;
  lucroCicloAtual: number;

  rodadasPorCiclo: number;
  rodadaAtual: number;
  percentualReinvestimento: number;

  cicloAtual: number;

  lucroRetiradoAcumulado: number;
  redAcumulado: number;
  resultadoLiquido: number;

  ciclosGreen: number;
  ciclosRed: number;
  maiorSequenciaGreen: number;
  maiorStake: number;

  status: StatusProjetoAlavancagem;
}

export interface CicloAlavancagem {
  id: number;
  projetoId: number;
  numero: number;

  valorInicial: number;
  stakeInicial: number;
  stakeFinal: number;
  lucroCiclo: number;
  valorRetirado: number;

  rodadasPlanejadas: number;
  rodadasRealizadas: number;

  status: StatusCicloAlavancagem;

  criadoEm?: string;
  fechadoEm?: string;
}

export interface ProjetoAlavancagemRequest {
  nome: string;
  descricao?: string;
  valorInicial: number;
  rodadasPorCiclo: number;
  percentualReinvestimento: number;
}
