import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  IonBadge,
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonContent,
  IonHeader,
  IonInput,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

import { Casa } from '../../models/casa';
import {
  MovimentacaoCasa,
  TipoMovimentacaoCasa,
} from '../../models/movimentacao-casa';
import { CasaService } from '../../services/casa.service';

@Component({
  selector: 'app-casas',
  templateUrl: './casas.page.html',
  styleUrls: ['./casas.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonButtons,
    IonMenuButton,
    IonContent,
    IonCard,
    IonCardContent,
    IonButton,
    IonInput,
    IonBadge,
  ],
})
export class CasasPage implements OnInit {
  casas: Casa[] = [];
  movimentacoes: MovimentacaoCasa[] = [];

  valoresApostadosAbertosPorCasa: Record<number, number> = {};

  carregando = false;
  modoEdicao = false;

  casaSelecionada?: Casa;

  form = {
    id: undefined as number | undefined,
    nome: '',
    saldoInicial: 0,
    ativa: true,
  };

  movimentoForm = {
    tipo: 'DEPOSITO' as TipoMovimentacaoCasa,
    valor: 0,
    descricao: '',
  };

  constructor(private casaService: CasaService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;

    this.casaService.listar().subscribe({
      next: (dados: Casa[]) => {
        this.casas = dados || [];
        this.carregando = false;

        this.carregarMovimentacoesTodasCasas();
        this.carregarValoresApostadosAbertos();
      },
      error: (erro: any) => {
        console.error(erro);
        this.carregando = false;
        alert('Erro ao carregar casas.');
      },
    });
  }

  carregarValoresApostadosAbertos(): void {
    this.valoresApostadosAbertosPorCasa = {};

    for (const casa of this.casas) {
      this.casaService.valorApostadoAberto(casa.id).subscribe({
        next: (valor: number) => {
          this.valoresApostadosAbertosPorCasa[casa.id] = Number(valor || 0);
        },
        error: (erro: any) => {
          console.error('Erro ao carregar valor apostado aberto:', erro);
          this.valoresApostadosAbertosPorCasa[casa.id] = 0;
        },
      });
    }
  }

  carregarMovimentacoesTodasCasas(): void {
    this.movimentacoes = [];

    for (const casa of this.casas) {
      this.casaService.listarMovimentacoes(casa.id).subscribe({
        next: (dados: MovimentacaoCasa[]) => {
          this.movimentacoes = [...this.movimentacoes, ...(dados || [])];
        },
        error: (erro: any) => {
          console.error(erro);
        },
      });
    }
  }

  salvarCasa(): void {
    if (!this.form.nome.trim()) {
      alert('Informe o nome da casa.');
      return;
    }

    const payload = {
      nome: this.form.nome.trim(),
      saldoInicial: Number(this.form.saldoInicial || 0),
      ativa: this.form.ativa,
    };

    if (this.modoEdicao && this.form.id) {
      this.casaService.atualizar(this.form.id, payload).subscribe({
        next: () => {
          this.limparCasa();
          this.carregar();
        },
        error: (erro: any) => {
          console.error(erro);
          alert('Erro ao atualizar casa.');
        },
      });

      return;
    }

    this.casaService.criar(payload).subscribe({
      next: () => {
        this.limparCasa();
        this.carregar();
      },
      error: (erro: any) => {
        console.error(erro);
        alert('Erro ao criar casa.');
      },
    });
  }

  editarCasa(casa: Casa): void {
    this.modoEdicao = true;

    this.form = {
      id: casa.id,
      nome: casa.nome,
      saldoInicial: casa.saldoInicial,
      ativa: casa.ativa,
    };

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  limparCasa(): void {
    this.modoEdicao = false;

    this.form = {
      id: undefined,
      nome: '',
      saldoInicial: 0,
      ativa: true,
    };
  }

  excluirCasa(casa: Casa): void {
    const confirmar = confirm(`Excluir ${casa.nome}?`);

    if (!confirmar) {
      return;
    }

    this.casaService.excluir(casa.id).subscribe({
      next: () => this.carregar(),
      error: (erro: any) => {
        console.error(erro);
        alert(
          'Não foi possível excluir. Se tiver movimentações, inative a casa.',
        );
      },
    });
  }

  selecionarCasa(casa: Casa): void {
    this.casaSelecionada = casa;

    this.movimentoForm = {
      tipo: 'DEPOSITO',
      valor: 0,
      descricao: '',
    };

    this.carregarMovimentacoes(casa.id);
  }

  carregarMovimentacoes(casaId: number): void {
    this.casaService.listarMovimentacoes(casaId).subscribe({
      next: (dados: MovimentacaoCasa[]) => {
        const outrasCasas = this.movimentacoes.filter(
          (mov) => mov.casaId !== casaId,
        );

        this.movimentacoes = [...outrasCasas, ...(dados || [])];
      },
      error: (erro: any) => {
        console.error(erro);
      },
    });
  }

  salvarMovimentacao(): void {
    if (!this.casaSelecionada) {
      alert('Selecione uma casa.');
      return;
    }

    if (!this.movimentoForm.valor || this.movimentoForm.valor <= 0) {
      alert('Informe um valor válido.');
      return;
    }

    this.casaService
      .movimentar(
        this.casaSelecionada.id,
        this.movimentoForm.tipo,
        Number(this.movimentoForm.valor),
        this.movimentoForm.descricao,
      )
      .subscribe({
        next: () => {
          const casaId = this.casaSelecionada!.id;

          this.movimentoForm = {
            tipo: 'DEPOSITO',
            valor: 0,
            descricao: '',
          };

          this.carregar();
          this.carregarMovimentacoes(casaId);
        },
        error: (erro: any) => {
          console.error(erro);
          alert('Erro ao lançar movimentação.');
        },
      });
  }

  totalPorTipo(casaId: number, tipo: TipoMovimentacaoCasa): number {
    return this.movimentacoes
      .filter((mov) => mov.casaId === casaId && mov.tipo === tipo)
      .reduce((total, mov) => total + Number(mov.valor || 0), 0);
  }

  valorApostadoAberto(casaId: number): number {
    return Number(this.valoresApostadosAbertosPorCasa[casaId] || 0);
  }

  lucroPerdaCasa(casaId: number): number {
    const green = this.totalPorTipo(casaId, 'GREEN');
    const red = this.totalPorTipo(casaId, 'RED');
    const cashout = this.totalPorTipo(casaId, 'CASHOUT');
    const ajuste = this.totalPorTipo(casaId, 'AJUSTE');

    return green - red + cashout + ajuste;
  }

  movimentacoesDaCasaSelecionada(): MovimentacaoCasa[] {
    if (!this.casaSelecionada) {
      return [];
    }

    return this.movimentacoes.filter(
      (mov) => mov.casaId === this.casaSelecionada!.id,
    );
  }

  formatarMoeda(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}
