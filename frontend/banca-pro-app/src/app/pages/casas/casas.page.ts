import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Aposta } from '../../models/aposta';
import { ApostaService } from '../../services/aposta.service';

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
  apostas: Aposta[] = [];
  filtroCasaId: number | null = null;
  movimentacoes: MovimentacaoCasa[] = [];

  valoresApostadosAbertosPorCasa: Record<number, number> = {};
  valoresAgendadosPorCasa: Record<number, number> = {};
  resultadosAtualizacaoSaldoPorCasa: Record<number, number> = {};

  carregando = false;
  modoEdicao = false;

  casaSelecionada?: Casa;

  form = {
    id: undefined as number | undefined,
    nome: '',
    saldoInicial: 0,
    saldoInicialTexto: 'R$ 0,00',
    linkSite: '',
    ativa: true,
  };

  quantidadesApostasAbertasPorCasa: Record<number, number> = {};

  movimentoForm = {
    tipo: 'DEPOSITO' as TipoMovimentacaoCasa,
    valor: 0,
    valorTexto: 'R$ 0,00',
    descricao: '',
  };

  constructor(
    private casaService: CasaService,
    private apostaService: ApostaService,
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;

    this.casaService.listar().subscribe({
      next: (dados: Casa[]) => {
        this.casas = dados || [];
        this.carregando = false;
        this.carregarApostas();

        this.carregarMovimentacoesTodasCasas();
        this.carregarValoresApostadosAbertos();
        this.carregarValoresAgendados();
        this.carregarResultadosAtualizacaoSaldo();
        this.carregarQuantidadesApostasAbertas();
      },
      error: (erro: any) => {
        console.error(erro);
        this.carregando = false;
        alert('Erro ao carregar casas.');
      },
    });
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

  carregarValoresApostadosAbertos(): void {
    this.valoresApostadosAbertosPorCasa = {};

    for (const casa of this.casas) {
      this.casaService.valorApostadoAberto(casa.id).subscribe({
        next: (valor: number) => {
          this.valoresApostadosAbertosPorCasa[casa.id] = Number(valor || 0);
        },
        error: () => {
          this.valoresApostadosAbertosPorCasa[casa.id] = 0;
        },
      });
    }
  }

  carregarValoresAgendados(): void {
    this.valoresAgendadosPorCasa = {};

    for (const casa of this.casas) {
      this.casaService.valorAgendado(casa.id).subscribe({
        next: (valor: number) => {
          this.valoresAgendadosPorCasa[casa.id] = Number(valor || 0);
        },
        error: () => {
          this.valoresAgendadosPorCasa[casa.id] = 0;
        },
      });
    }
  }

  carregarResultadosAtualizacaoSaldo(): void {
    this.resultadosAtualizacaoSaldoPorCasa = {};

    for (const casa of this.casas) {
      this.casaService.resultadoAtualizacaoSaldoHoje(casa.id).subscribe({
        next: (valor: number) => {
          this.resultadosAtualizacaoSaldoPorCasa[casa.id] = Number(valor || 0);
        },
        error: () => {
          this.resultadosAtualizacaoSaldoPorCasa[casa.id] = 0;
        },
      });
    }
  }

  atualizarSaldoCasa(casa: Casa): void {
    const saldoDigitado = prompt(
      `Informe o saldo atual de ${casa.nome}:`,
      this.formatarNumeroParaInput(casa.saldoAtual || 0),
    );

    if (saldoDigitado === null) {
      return;
    }

    const retiradaDigitada = prompt(
      `Informe a retirada pessoal/saque para banco de ${casa.nome}:\n\n` +
        'Se não houve retirada, deixe 0.',
      '0,00',
    );

    if (retiradaDigitada === null) {
      return;
    }

    const novoSaldo = this.converterMoedaParaNumero(saldoDigitado);
    const retiradaPessoal = this.converterMoedaParaNumero(retiradaDigitada);

    if (Number.isNaN(novoSaldo) || novoSaldo < 0) {
      alert('Informe um saldo válido.');
      return;
    }

    if (Number.isNaN(retiradaPessoal) || retiradaPessoal < 0) {
      alert('Informe uma retirada válida.');
      return;
    }

    this.casaService
      .atualizarSaldo(casa.id, novoSaldo, retiradaPessoal)
      .subscribe({
        next: () => {
          this.carregar();
        },

        error: (erro: any) => {
          console.error('Erro completo:', erro);

          const mensagem =
            erro?.error?.message ||
            erro?.message ||
            'Não foi possível conectar ao backend. Verifique se a API está rodando.';

          alert(mensagem);
        },
      });
  }

  salvarCasa(): void {
    if (!this.form.nome.trim()) {
      alert('Informe o nome da casa.');
      return;
    }

    const payload = {
      nome: this.form.nome.trim(),
      saldoInicial: Number(this.form.saldoInicial || 0),
      linkSite: this.form.linkSite.trim(),
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
      saldoInicialTexto: this.formatarMoeda(casa.saldoInicial),
      linkSite: casa.linkSite || '',
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
      saldoInicialTexto: 'R$ 0,00',
      linkSite: '',
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
      valorTexto: 'R$ 0,00',
      descricao: '',
    };

    this.carregarMovimentacoes(casa.id);

    setTimeout(() => {
      const elemento = document.querySelector('.mov-card');
      elemento?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
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

    const valor = this.converterMoedaParaNumero(this.movimentoForm.valorTexto);

    if (!valor || valor <= 0) {
      alert('Informe um valor válido.');
      return;
    }

    this.casaService
      .movimentar(
        this.casaSelecionada.id,
        this.movimentoForm.tipo,
        valor,
        this.movimentoForm.descricao,
      )
      .subscribe({
        next: () => {
          const casaId = this.casaSelecionada!.id;

          this.movimentoForm = {
            tipo: 'DEPOSITO',
            valor: 0,
            valorTexto: 'R$ 0,00',
            descricao: '',
          };

          this.casaService.listar().subscribe({
            next: (casas: Casa[]) => {
              this.casas = casas || [];

              const casaAtualizada = this.casas.find(
                (casa) => casa.id === casaId,
              );

              if (casaAtualizada) {
                this.casaSelecionada = casaAtualizada;
              }

              this.carregarMovimentacoesTodasCasas();
              this.carregarValoresApostadosAbertos();
              this.carregarValoresAgendados();
              this.carregarResultadosAtualizacaoSaldo();
              this.carregarQuantidadesApostasAbertas();
            },
            error: (erro: any) => {
              console.error('Erro ao recarregar casas:', erro);
            },
          });
        },
        error: (erro: any) => {
          console.error(erro);

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao lançar movimentação.';

          alert(mensagem);
        },
      });
  }

  onSaldoInicialInput(event: any): void {
    const texto = event.detail.value || '';
    const numeros = texto.replace(/\D/g, '');
    const valor = Number(numeros) / 100;

    this.form.saldoInicial = valor;
    this.form.saldoInicialTexto = this.formatarMoeda(valor);
  }

  totalPorTipo(casaId: number, tipo: TipoMovimentacaoCasa): number {
    return this.movimentacoes
      .filter((mov) => mov.casaId === casaId && mov.tipo === tipo)
      .reduce((total, mov) => total + Number(mov.valor || 0), 0);
  }

  valorApostadoAberto(casaId: number): number {
    return Number(this.valoresApostadosAbertosPorCasa[casaId] || 0);
  }

  valorAgendado(casaId: number): number {
    return Number(this.valoresAgendadosPorCasa[casaId] || 0);
  }

  resultadoAtualizacaoSaldo(casaId: number): number {
    return Number(this.resultadosAtualizacaoSaldoPorCasa[casaId] || 0);
  }

  lucroPerdaCasa(casaId: number): number {
    const lucroApostasFechadas = this.apostas
      .filter((aposta) => Number(aposta.casaId) === Number(casaId))
      .filter((aposta) => aposta.status === 'FECHADA')
      .reduce((total, aposta) => total + Number(aposta.lucroPrejuizo || 0), 0);

    const resultadoAtualizacao = this.resultadoAtualizacaoSaldo(casaId);

    return lucroApostasFechadas + resultadoAtualizacao;
  }

  movimentacoesDaCasaSelecionada(): MovimentacaoCasa[] {
    if (!this.casaSelecionada) {
      return [];
    }

    return this.movimentacoes.filter(
      (mov) => mov.casaId === this.casaSelecionada!.id,
    );
  }

  converterMoedaParaNumero(valor: string): number {
    if (!valor) {
      return 0;
    }

    let texto = valor.replace('R$', '').replace(/\s/g, '').trim();

    if (texto.includes(',') && texto.includes('.')) {
      texto = texto.replace(/\./g, '').replace(',', '.');
    } else if (texto.includes(',')) {
      texto = texto.replace(',', '.');
    }

    return Number(texto);
  }

  formatarNumeroParaInput(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  }

  formatarMoeda(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  formatarMoedaInput(event: any): void {
    let valor = event.target.value || '';

    valor = valor.replace(/\D/g, '');

    valor = (Number(valor) / 100).toFixed(2);

    valor = valor.replace('.', ',');

    valor = valor.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    event.target.value = `R$ ${valor}`;
  }

  onMovimentoValorInput(event: any): void {
    let valor = event.detail.value || '';

    valor = valor.replace(/\D/g, '');

    const numero = Number(valor) / 100;

    this.movimentoForm.valor = numero;
    this.movimentoForm.valorTexto = this.formatarMoeda(numero);
  }

  cancelarMovimentacao(): void {
    this.casaSelecionada = undefined;

    this.movimentoForm = {
      tipo: 'DEPOSITO',
      valor: 0,
      valorTexto: 'R$ 0,00',
      descricao: '',
    };
  }

  casasFiltradas(): Casa[] {
    if (!this.filtroCasaId) {
      return this.casas;
    }

    return this.casas.filter((casa) => casa.id === this.filtroCasaId);
  }

  quantidadeApostasAbertas(casaId: number): number {
    return Number(this.quantidadesApostasAbertasPorCasa[casaId] || 0);
  }
  carregarQuantidadesApostasAbertas(): void {
    this.quantidadesApostasAbertasPorCasa = {};

    this.apostaService.listar().subscribe({
      next: (apostas: Aposta[]) => {
        for (const aposta of apostas || []) {
          const aberta =
            aposta.status === 'APOSTA_FEITA' ||
            aposta.status === 'EM_ANDAMENTO';

          if (!aberta) {
            continue;
          }

          const casaId = Number(aposta.casaId);

          this.quantidadesApostasAbertasPorCasa[casaId] =
            Number(this.quantidadesApostasAbertasPorCasa[casaId] || 0) + 1;
        }
      },
      error: (erro: any) => {
        console.error('Erro ao carregar quantidade de apostas abertas:', erro);
      },
    });
  }

  carregarApostas(): void {
    this.apostaService.listar().subscribe({
      next: (apostas: Aposta[]) => {
        this.apostas = apostas || [];
      },
      error: (erro: any) => {
        console.error('Erro ao carregar apostas:', erro);
        this.apostas = [];
      },
    });
  }

  abrirSiteCasa(casa: Casa): void {
    let link = (casa.linkSite || '').trim();

    if (!link) {
      alert('Essa casa não possui site cadastrado.');
      return;
    }

    if (!link.startsWith('http://') && !link.startsWith('https://')) {
      link = 'https://' + link;
    }

    window.open(link, '_blank');
  }
}
