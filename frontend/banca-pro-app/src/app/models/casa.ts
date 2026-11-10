export interface Casa {
  id: number;
  nome: string;
  saldoInicial: number;
  saldoAtual: number;
  ativa: boolean;
  createdAt?: string;
  updatedAt?: string;
  linkSite?: string;
}
