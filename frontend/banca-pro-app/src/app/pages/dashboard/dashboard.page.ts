import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  IonButtons,
  IonContent,
  IonHeader,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

import { DashboardResumo } from '../../models/dashboard-resumo';
import { DiaOperacional } from '../../models/dia-operacional';
import { DashboardService } from '../../services/dashboard.service';
import { DiaOperacionalService } from '../../services/dia-operacional.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonButtons,
    IonMenuButton,
    IonContent,
  ],
})
export class DashboardPage implements OnInit {
  resumo: DashboardResumo = {
    caixaAtual: 0,
    saldoTotalCasas: 0,
    patrimonioTotal: 0,
    totalApostado: 0,
    lucroPrejuizo: 0,
    quantidadeJogosAgendados: 0,
    valorTotalJogosAgendados: 0,

    apostasEmAberto: 0,
    recuperacaoPotencial: 0,
    projecaoDia: 0,
  };

  diaAtual?: DiaOperacional;
  historicoDias: DiaOperacional[] = [];
  carregandoDia = false;

  constructor(
    private dashboardService: DashboardService,
    private diaOperacionalService: DiaOperacionalService,
  ) {}

  ngOnInit(): void {
    this.carregarResumo();
  }

  carregarResumo(): void {
    this.dashboardService.resumo().subscribe({
      next: (dados: DashboardResumo) => {
        this.resumo = dados;
      },
      error: (erro: any) => {
        console.error('Erro ao carregar dashboard:', erro);
      },
    });

    this.carregarDiaOperacional();
    this.carregarHistoricoDias();
  }

  carregarDiaOperacional(): void {
    this.carregandoDia = true;

    this.diaOperacionalService.atual().subscribe({
      next: (dia: DiaOperacional) => {
        this.diaAtual = dia;
        this.carregandoDia = false;
      },
      error: (erro: any) => {
        console.error('Erro ao carregar dia operacional:', erro);
        this.carregandoDia = false;
      },
    });
  }

  carregarHistoricoDias(): void {
    this.diaOperacionalService.historico().subscribe({
      next: (dias: DiaOperacional[]) => {
        this.historicoDias = dias || [];
      },
      error: (erro: any) => {
        console.error('Erro ao carregar histórico diário:', erro);
      },
    });
  }

  fecharDia(): void {
    if (!this.diaAtual || this.diaAtual.status !== 'ABERTO') {
      alert('Não existe dia operacional aberto para fechar.');
      return;
    }

    const confirmar = confirm(
      `Deseja fechar o dia ${this.formatarData(this.diaAtual.data)}?\n\n` +
        `Saldo final atual: ${this.formatarMoeda(this.diaAtual.saldoFinal)}\n` +
        'Após fechar, você poderá iniciar o próximo dia.',
    );

    if (!confirmar) {
      return;
    }

    this.diaOperacionalService.fechar().subscribe({
      next: (dia: DiaOperacional) => {
        this.diaAtual = dia;
        this.carregarHistoricoDias();
        alert('Dia fechado com sucesso.');
      },
      error: (erro: any) => {
        console.error('Erro ao fechar dia:', erro);
        alert('Erro ao fechar dia operacional.');
      },
    });
  }

  iniciarProximoDia(): void {
    const confirmar = confirm(
      'Deseja iniciar o próximo dia operacional usando o saldo final do último dia fechado?',
    );

    if (!confirmar) {
      return;
    }

    this.diaOperacionalService.iniciarProximo().subscribe({
      next: (dia: DiaOperacional) => {
        this.diaAtual = dia;
        this.carregarResumo();
        alert('Novo dia operacional iniciado.');
      },
      error: (erro: any) => {
        console.error('Erro ao iniciar próximo dia:', erro);
        alert('Erro ao iniciar próximo dia operacional.');
      },
    });
  }

  zerarTudo(): void {
    const confirmar = confirm(
      'ATENÇÃO!\n\nDeseja apagar todas as apostas, movimentações e zerar os saldos das casas?',
    );

    if (!confirmar) {
      return;
    }

    this.dashboardService.zerarTudo().subscribe({
      next: () => {
        alert('Base zerada com sucesso.');
        this.carregarResumo();
      },
      error: (erro: any) => {
        console.error('Erro ao zerar base:', erro);
        alert('Erro ao zerar base.');
      },
    });
  }

  podeFecharDia(): boolean {
    return this.diaAtual?.status === 'ABERTO';
  }

  podeIniciarProximoDia(): boolean {
    return this.diaAtual?.status === 'FECHADO';
  }

  statusDiaLabel(status?: string): string {
    if (status === 'ABERTO') {
      return 'Aberto';
    }

    if (status === 'FECHADO') {
      return 'Fechado';
    }

    return 'Não iniciado';
  }

  formatarMoeda(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
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
}
