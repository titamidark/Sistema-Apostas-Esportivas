import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
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

import {
  CaixaResumo,
  MovimentacaoCaixa,
  TipoMovimentacaoCaixa,
} from '../../models/caixa';

import { DiaOperacional } from '../../models/dia-operacional';
import { CaixaService } from '../../services/caixa.service';
import { DiaOperacionalService } from '../../services/dia-operacional.service';

@Component({
  selector: 'app-caixa',
  templateUrl: './caixa.page.html',
  styleUrls: ['./caixa.page.scss'],
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
  ],
})
export class CaixaPage implements OnInit {
  resumo: CaixaResumo = {
    saldoInicial: 0,
    adicionado: 0,
    saidas: 0,
    apostado: 0,
    transferidoParaCasas: 0,
    saqueDasCasas: 0,
    ajustes: 0,
    saldoAtual: 0,
  };

  movimentacoes: MovimentacaoCaixa[] = [];

  form = {
    tipo: 'SALDO_INICIAL_DIA' as TipoMovimentacaoCaixa,
    valor: 0,
    valorTexto: 'R$ 0,00',
    descricao: 'Abertura do caixa do dia',
  };

  carregando = false;
  diaAtual?: DiaOperacional;

  constructor(
    private caixaService: CaixaService,
    private diaOperacionalService: DiaOperacionalService,
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;

    this.diaOperacionalService.atual().subscribe({
      next: (dia: DiaOperacional) => {
        this.diaAtual = dia;
      },
      error: (erro: any) => {
        console.error('Erro ao carregar dia operacional:', erro);
      },
    });

    this.caixaService.resumo().subscribe({
      next: (dados: CaixaResumo) => {
        this.resumo = dados;

        if (this.resumo.saldoInicial > 0) {
          this.form.tipo = 'ENTRADA_MANUAL';

          if (this.form.descricao === 'Abertura do caixa do dia') {
            this.form.descricao = '';
          }
        } else {
          this.form.tipo = 'SALDO_INICIAL_DIA';

          if (!this.form.descricao) {
            this.form.descricao = 'Abertura do caixa do dia';
          }
        }

        this.carregando = false;
      },
      error: (erro: any) => {
        console.error(erro);
        this.carregando = false;
      },
    });

    this.caixaService.listarMovimentacoes().subscribe({
      next: (dados: MovimentacaoCaixa[]) => {
        this.movimentacoes = dados || [];
      },
      error: (erro: any) => {
        console.error(erro);
      },
    });
  }

  salvarMovimentacao(): void {
    if (this.diaFechado()) {
      alert('O dia operacional está fechado. Inicie o próximo dia para lançar novas movimentações.');
      return;
    }

    if (!this.form.valor || this.form.valor <= 0) {
      alert('Informe um valor maior que zero.');
      return;
    }

    this.caixaService
      .movimentar(this.form.tipo, Number(this.form.valor), this.form.descricao)
      .subscribe({
        next: () => {
          this.form = {
            tipo: 'ENTRADA_MANUAL',
            valor: 0,
            valorTexto: 'R$ 0,00',
            descricao: '',
          };

          this.carregar();
        },
        error: (erro: any) => {
          console.error(erro);
          alert('Erro ao salvar movimentação.');
        },
      });
  }

  onValorInput(event: any): void {
    const texto = event.detail.value || '';
    const apenasNumeros = texto.replace(/\D/g, '');

    const valor = Number(apenasNumeros) / 100;

    this.form.valor = valor;
    this.form.valorTexto = this.formatarMoeda(valor);
  }

  saldoInicialDefinido(): boolean {
    return this.resumo.saldoInicial > 0 || this.diaAtual?.status === 'FECHADO';
  }

  diaFechado(): boolean {
    return this.diaAtual?.status === 'FECHADO';
  }

  statusDiaLabel(): string {
    if (this.diaAtual?.status === 'ABERTO') {
      return 'Aberto';
    }

    if (this.diaAtual?.status === 'FECHADO') {
      return 'Fechado';
    }

    return 'Não iniciado';
  }

  formatarData(data?: string): string {
    if (!data) {
      return '-';
    }

    const partes = data.split('-');

    if (partes.length === 3) {
      return `${partes[2]}/${partes[1]}/${partes[0]}`;
    }

    return data;
  }

  classeValor(tipo: TipoMovimentacaoCaixa): string {
    if (
      tipo === 'SALDO_INICIAL_DIA' ||
      tipo === 'ENTRADA_MANUAL' ||
      tipo === 'SAQUE_CASA' ||
      tipo === 'AJUSTE'
    ) {
      return 'green';
    }

    return 'red';
  }

  labelTipo(tipo: TipoMovimentacaoCaixa): string {
    const labels: Record<TipoMovimentacaoCaixa, string> = {
      SALDO_INICIAL_DIA: 'Saldo Inicial do Dia',
      ENTRADA_MANUAL: 'Entrada Manual',
      SAIDA_MANUAL: 'Saída Manual',
      APOSTA: 'Aposta',
      DEPOSITO_CASA: 'Depósito na Casa',
      SAQUE_CASA: 'Saque da Casa',
      AJUSTE: 'Ajuste',
    };

    return labels[tipo];
  }

  formatarMoeda(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}
