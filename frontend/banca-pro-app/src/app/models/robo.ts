export interface Robo {
  id: number;
  nome: string;
  ativo: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface MetodoRobo {
  id: number;
  roboId: number;
  nome: string;
  descricao?: string;
  ativo: boolean;
  createdAt?: string;
  updatedAt?: string;
}
