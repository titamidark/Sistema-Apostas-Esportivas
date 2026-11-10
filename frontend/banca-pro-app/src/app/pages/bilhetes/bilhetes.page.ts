import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DiaOperacional } from '../../models/dia-operacional';
import { DiaOperacionalService } from '../../services/dia-operacional.service';

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

import { Aposta, ResultadoAposta, TipoAposta } from '../../models/aposta';
import { Casa } from '../../models/casa';
import { Tipster } from '../../models/tipster';
import { MetodoRobo, Robo } from '../../models/robo';

import { ApostaService } from '../../services/aposta.service';
import { CasaService } from '../../services/casa.service';
import { TipsterService } from '../../services/tipster.service';
import { RoboService } from '../../services/robo.service';

type OrigemApostaForm = 'TIPSTER' | 'ROBO';

@Component({
  selector: 'app-bilhetes',
  templateUrl: './bilhetes.page.html',
  styleUrls: ['./bilhetes.page.scss'],
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
export class BilhetesPage implements OnInit {
  apostas: Aposta[] = [];
  diaOperacional?: DiaOperacional;
  casas: Casa[] = [];
  tipsters: Tipster[] = [];
  robos: Robo[] = [];
  metodosRobo: MetodoRobo[] = [];
  @ViewChild('valorApostadoInput')
  valorApostadoInput?: IonInput;

  modoEdicao = false;
  apostaEditandoId?: number;

  novaObservacaoDia = '';
  observacoesDia: string[] = [];

  filtros = {
    casaId: 0,
    tipster: '',
    status: '',
    data: this.dataHojeInput(),
    hora: '',
  };

  form = this.novoFormulario();

  constructor(
    private apostaService: ApostaService,
    private casaService: CasaService,
    private tipsterService: TipsterService,
    private roboService: RoboService,
    private diaOperacionalService: DiaOperacionalService,
  ) {}

  ngOnInit(): void {
    this.carregarDiaOperacional();
    this.carregar();
  }
  novoFormulario() {
    return {
      casaId: 0,
      origem: 'TIPSTER' as OrigemApostaForm,
      tipster: '',
      roboId: 0,
      metodoRoboId: 0,
      tipo: 'AGENDADA' as TipoAposta,
      mercado: '',
      palpite: '',
      valorApostado: 0,
      valorApostadoTexto: 'R$ 0,00',
      oddTipster: 0,
      oddTipsterTexto: '',
      oddFeita: 0,
      oddFeitaTexto: '',
      retornoPrevistoManual: 0,
      retornoPrevistoManualTexto: 'R$ 0,00',
      usarRetornoManual: false,
      linkPrint: '',
      horaJogo: '',
    };
  }

  carregar(): void {
    this.apostaService.listar().subscribe({
      next: (dados: Aposta[]) => {
        this.apostas = this.ordenarPorHorario(dados || []);
      },
      error: (erro: any) => console.error(erro),
    });

    this.casaService.listar().subscribe({
      next: (dados: Casa[]) => {
        this.casas = dados || [];

        if (!this.form.casaId && this.casas.length > 0) {
          this.form.casaId = this.casas[0].id;
        }
      },
      error: (erro: any) => console.error(erro),
    });

    this.tipsterService.listar().subscribe({
      next: (dados: Tipster[]) => {
        this.tipsters = dados || [];
      },
      error: (erro: any) => console.error(erro),
    });

    this.roboService.listarRobos().subscribe({
      next: (dados: Robo[]) => {
        this.robos = dados || [];
      },
      error: (erro: any) => console.error(erro),
    });
  }

  onOrigemChange(): void {
    if (this.form.origem === 'ROBO') {
      this.form.tipo = 'RAPIDA';
      this.form.tipster = 'ROBÔ';
      this.form.mercado = '';
      this.form.palpite = '';
      this.form.linkPrint = '';
      this.form.oddTipster = 0;
      this.form.oddTipsterTexto = '';
    } else {
      this.form.tipo = 'AGENDADA';
      this.form.tipster = '';
      this.form.roboId = 0;
      this.form.metodoRoboId = 0;
      this.metodosRobo = [];
    }
  }

  onRoboChange(): void {
    this.form.metodoRoboId = 0;
    this.metodosRobo = [];

    if (!this.form.roboId) {
      return;
    }

    this.roboService.listarMetodosPorRobo(Number(this.form.roboId)).subscribe({
      next: (dados: MetodoRobo[]) => {
        this.metodosRobo = dados || [];
      },
      error: (erro: any) => console.error(erro),
    });
  }

  apostasFiltradas(): Aposta[] {
    return this.apostas.filter((aposta) => {
      if (aposta.status === 'CANCELADA') {
        return false;
      }

      const casaFiltro = Number(this.filtros.casaId || 0);

      const casaOk = casaFiltro === 0 || Number(aposta.casaId) === casaFiltro;

      const tipsterOk =
        !this.filtros.tipster || aposta.tipster === this.filtros.tipster;

      const statusOk =
        !this.filtros.status || aposta.status === this.filtros.status;

      const dataOk =
        !this.filtros.data ||
        this.dataApostaInput(aposta) === this.filtros.data;

      const horaOk =
        !this.filtros.hora || this.horarioJogo(aposta) === this.filtros.hora;

      return casaOk && tipsterOk && statusOk && dataOk && horaOk;
    });
  }

  limparFiltros(): void {
    this.filtros = {
      casaId: 0,
      tipster: '',
      status: '',
      data: this.dataOperacionalInput(),
      hora: '',
    };
  }

  ordenarPorHorario(apostas: Aposta[]): Aposta[] {
    return [...apostas].sort((a, b) => {
      const horaA = this.valorOrdenacaoHorario(a);
      const horaB = this.valorOrdenacaoHorario(b);

      return horaA - horaB;
    });
  }

  valorOrdenacaoHorario(aposta: Aposta): number {
    if (!aposta.dataJogo) {
      return Number.MAX_SAFE_INTEGER;
    }

    const data = new Date(aposta.dataJogo);
    const tempo = data.getTime();

    if (Number.isNaN(tempo)) {
      return Number.MAX_SAFE_INTEGER;
    }

    return tempo;
  }

  salvar(): void {
    if (!this.form.casaId) {
      alert('Selecione uma casa.');
      return;
    }

    if (this.form.origem === 'TIPSTER' && !this.form.tipster.trim()) {
      alert('Selecione o tipster.');
      return;
    }

    if (this.form.origem === 'ROBO' && !this.form.roboId) {
      alert('Selecione o robô.');
      return;
    }

    if (this.form.origem === 'ROBO' && !this.form.metodoRoboId) {
      alert('Selecione o método do robô.');
      return;
    }

    if (this.form.origem === 'TIPSTER' && !this.form.mercado.trim()) {
      alert('Informe o mercado.');
      return;
    }

    if (!this.form.valorApostado || this.form.valorApostado <= 0) {
      alert('Informe o valor apostado.');
      return;
    }

    if (!this.form.oddFeita || this.form.oddFeita <= 0) {
      alert('Informe a odd feita.');
      return;
    }
    if (
      this.form.origem === 'TIPSTER' &&
      this.form.tipo === 'AGENDADA' &&
      !this.form.linkPrint.trim()
    ) {
      alert('Informe o link da aposta agendada.');
      return;
    }

    const payload: any = {
      casaId: this.form.casaId,
      origem: this.form.origem,
      tipster: this.form.origem === 'TIPSTER' ? this.form.tipster : 'ROBÔ',
      roboId: this.form.origem === 'ROBO' ? Number(this.form.roboId) : null,
      metodoRoboId:
        this.form.origem === 'ROBO' ? Number(this.form.metodoRoboId) : null,
      tipo: this.form.origem === 'ROBO' ? 'RAPIDA' : this.form.tipo,
      mercado:
        this.form.origem === 'ROBO'
          ? this.nomeMetodoRobo(this.form.metodoRoboId)
          : this.form.mercado,
      palpite: this.form.origem === 'ROBO' ? '' : this.form.palpite,
      valorApostado: Number(this.form.valorApostado),
      oddTipster:
        this.form.origem === 'ROBO' ? 0 : Number(this.form.oddTipster || 0),
      oddFeita: Number(this.form.oddFeita),
      retornoPrevisto: this.retornoPrevisto(),
      linkPrint: this.form.origem === 'ROBO' ? '' : this.form.linkPrint,
      dataJogo: this.montarDataHoraHoje(this.form.horaJogo),
    };

    if (this.modoEdicao && this.apostaEditandoId) {
      this.apostaService.atualizar(this.apostaEditandoId, payload).subscribe({
        next: () => {
          this.limpar();
          this.carregar();
        },

        error: (erro: any) => {
          console.error(erro);

          const mensagem =
            erro?.error?.message || erro?.error || 'Erro ao atualizar aposta.';

          alert(mensagem);
        },
      });

      return;
    }

    this.apostaService.criar(payload).subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },

      error: (erro: any) => {
        console.error(erro);

        const mensagem =
          erro?.error?.message || erro?.error || 'Erro ao criar aposta.';

        alert(mensagem);
      },
    });
  }

  editar(aposta: Aposta): void {
    if (aposta.status === 'FECHADA') {
      alert('Aposta fechada não pode ser editada.');
      return;
    }

    this.modoEdicao = true;
    this.apostaEditandoId = aposta.id;

    this.form = {
      casaId: aposta.casaId,
      origem: (aposta.origem || 'TIPSTER') as OrigemApostaForm,
      tipster: aposta.tipster || '',
      roboId: Number(aposta.roboId || 0),
      metodoRoboId: Number(aposta.metodoRoboId || 0),
      tipo: aposta.tipo,
      mercado: aposta.mercado || '',
      palpite: aposta.palpite || '',

      valorApostado: Number(aposta.valorApostado || 0),
      valorApostadoTexto: this.formatarMoeda(aposta.valorApostado),

      oddTipster: Number(aposta.oddTipster || 0),
      oddTipsterTexto: aposta.oddTipster
        ? Number(aposta.oddTipster).toFixed(2)
        : '',

      oddFeita: Number(aposta.oddFeita || 0),
      oddFeitaTexto: aposta.oddFeita ? Number(aposta.oddFeita).toFixed(2) : '',

      retornoPrevistoManual: 0,
      retornoPrevistoManualTexto: 'R$ 0,00',
      usarRetornoManual: false,

      linkPrint: aposta.linkPrint || '',
      horaJogo: this.extrairHora(aposta.dataJogo),
    };

    this.form.retornoPrevistoManual = this.retornoPrevisto();
    this.form.retornoPrevistoManualTexto = this.formatarMoeda(
      this.retornoPrevisto(),
    );

    if (this.form.origem === 'ROBO' && this.form.roboId) {
      this.roboService
        .listarMetodosPorRobo(Number(this.form.roboId))
        .subscribe({
          next: (dados: MetodoRobo[]) => {
            this.metodosRobo = dados || [];
            this.form.metodoRoboId = Number(aposta.metodoRoboId || 0);
          },
          error: (erro: any) => console.error(erro),
        });
    }

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
    setTimeout(() => {
      this.valorApostadoInput?.setFocus();
    }, 600);
  }

  limpar(): void {
    this.modoEdicao = false;
    this.apostaEditandoId = undefined;
    this.metodosRobo = [];
    this.form = this.novoFormulario();

    if (this.casas.length > 0) {
      this.form.casaId = this.casas[0].id;
    }
  }

  onValorApostadoInput(event: any): void {
    const texto = event.detail.value || '';
    const numeros = texto.replace(/\D/g, '');
    const valor = Number(numeros) / 100;

    this.form.valorApostado = valor;
    this.form.valorApostadoTexto = this.formatarMoeda(valor);

    if (!this.form.usarRetornoManual) {
      this.form.retornoPrevistoManual = this.retornoPrevisto();
      this.form.retornoPrevistoManualTexto = this.formatarMoeda(
        this.retornoPrevisto(),
      );
    }
  }

  onOddTipsterInput(event: any): void {
    const texto = event.detail.value || '';
    const numeros = texto.replace(/\D/g, '');

    if (!numeros) {
      this.form.oddTipster = 0;
      this.form.oddTipsterTexto = '';
      return;
    }

    const valor = Number(numeros) / 100;
    this.form.oddTipster = valor;
    this.form.oddTipsterTexto = valor.toFixed(2);
  }

  onOddFeitaInput(event: any): void {
    const texto = event.detail.value || '';
    const numeros = texto.replace(/\D/g, '');

    if (!numeros) {
      this.form.oddFeita = 0;
      this.form.oddFeitaTexto = '';
      return;
    }

    const valor = Number(numeros) / 100;
    this.form.oddFeita = valor;
    this.form.oddFeitaTexto = valor.toFixed(2);

    if (!this.form.usarRetornoManual) {
      this.form.retornoPrevistoManual = this.retornoPrevisto();
      this.form.retornoPrevistoManualTexto = this.formatarMoeda(
        this.retornoPrevisto(),
      );
    }
  }

  onRetornoPrevistoInput(event: any): void {
    const texto = event.detail.value || '';
    const numeros = texto.replace(/\D/g, '');
    const valor = Number(numeros) / 100;

    this.form.retornoPrevistoManual = valor;
    this.form.retornoPrevistoManualTexto = this.formatarMoeda(valor);
    this.form.usarRetornoManual = true;
  }

  montarDataHoraHoje(hora: string): string | null {
    if (!hora) {
      return null;
    }

    if (this.diaOperacional?.data) {
      return `${this.diaOperacional.data}T${hora}:00`;
    }

    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');

    return `${ano}-${mes}-${dia}T${hora}:00`;
  }

  extrairHora(dataJogo?: string): string {
    if (!dataJogo) {
      return '';
    }

    const data = new Date(dataJogo);

    if (Number.isNaN(data.getTime())) {
      return '';
    }

    const hora = String(data.getHours()).padStart(2, '0');
    const minuto = String(data.getMinutes()).padStart(2, '0');

    return `${hora}:${minuto}`;
  }

  horarioJogo(aposta: Aposta): string {
    if (!aposta.dataJogo) {
      return '-';
    }

    const data = new Date(aposta.dataJogo);

    if (Number.isNaN(data.getTime())) {
      return '-';
    }

    return data.toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  fazerAposta(aposta: Aposta): void {
    this.apostaService.fazerAposta(aposta.id).subscribe({
      next: () => {
        this.carregar();
      },
      error: (erro: any) => {
        console.error(erro);

        const mensagem =
          erro?.error || erro?.error?.message || 'Erro ao fazer aposta.';

        alert(mensagem);

        this.carregar();
      },
    });
  }
  fechar(aposta: Aposta, resultado: ResultadoAposta): void {
    let retornoReal = 0;

    if (resultado === 'GREEN') {
      retornoReal = aposta.retornoPrevisto;
    }

    if (resultado === 'RED') {
      retornoReal = 0;
    }

    if (resultado === 'CASHOUT') {
      const valor = prompt('Informe o valor recebido no cashout:');

      if (valor === null) {
        return;
      }

      retornoReal = Number(valor.replace(',', '.'));
    }

    this.apostaService.fechar(aposta.id, resultado, retornoReal).subscribe({
      next: () => this.carregar(),
      error: (erro: any) => {
        console.error(erro);
        alert('Erro ao fechar aposta.');
      },
    });
  }

  cancelar(aposta: Aposta): void {
    const confirmar = confirm(
      `Cancelar aposta #${aposta.id}?\n\n` +
        'Se ela já foi feita, o valor será devolvido ao caixa.',
    );

    if (!confirmar) {
      return;
    }

    this.apostaService.cancelar(aposta.id).subscribe({
      next: () => this.carregar(),
      error: (erro: any) => {
        console.error(erro);
        alert('Erro ao cancelar aposta.');
      },
    });
  }

  reabrir(aposta: Aposta): void {
    const confirmar = confirm(
      `Reabrir aposta #${aposta.id}?\n\n` +
        'Ela voltará para APOSTA_FEITA e poderá ser fechada novamente.',
    );

    if (!confirmar) {
      return;
    }

    this.apostaService.reabrir(aposta.id).subscribe({
      next: () => this.carregar(),
      error: (erro: any) => {
        console.error(erro);
        alert('Erro ao reabrir aposta.');
      },
    });
  }

  abrirLink(aposta: Aposta): void {
    let link = (aposta.linkPrint || '').trim();

    if (!link) {
      alert('Essa aposta não possui link/print.');
      return;
    }

    if (!link.startsWith('http://') && !link.startsWith('https://')) {
      link = 'https://' + link;
    }

    const novaAba = window.open('', '_blank');

    if (novaAba) {
      novaAba.location.href = link;
    } else {
      alert(
        'O navegador bloqueou a abertura do link. Libere pop-ups para o app.',
      );
    }
  }

  abrirObservacoes(aposta: Aposta): void {
    alert(aposta.palpite || 'Sem observações.');
  }

  nomeCasa(casaId: number): string {
    const casa = this.casas.find((c) => c.id === casaId);
    return casa ? casa.nome : `Casa #${casaId}`;
  }

  nomeRobo(roboId?: number): string {
    if (!roboId) {
      return '-';
    }

    const robo = this.robos.find((item) => Number(item.id) === Number(roboId));

    return robo ? robo.nome : `Robô #${roboId}`;
  }

  nomeMetodoRobo(metodoId?: number): string {
    if (!metodoId) {
      return 'Método Robô';
    }

    const metodo = this.metodosRobo.find(
      (item) => Number(item.id) === Number(metodoId),
    );

    return metodo ? metodo.nome : `Método #${metodoId}`;
  }

  retornoPrevisto(): number {
    if (this.form.usarRetornoManual) {
      return Number(this.form.retornoPrevistoManual || 0);
    }

    return (
      Number(this.form.valorApostado || 0) * Number(this.form.oddFeita || 0)
    );
  }

  quantidadeFiltrada(): number {
    return this.apostasFiltradas().length;
  }

  valorTotalFiltrado(): number {
    return this.apostasFiltradas().reduce(
      (total, aposta) => total + Number(aposta.valorApostado || 0),
      0,
    );
  }

  possivelLucro(): number {
    return this.retornoPrevisto() - Number(this.form.valorApostado || 0);
  }

  podeFazer(aposta: Aposta): boolean {
    return aposta.status === 'AGENDADA';
  }

  podeFechar(aposta: Aposta): boolean {
    return aposta.status === 'APOSTA_FEITA' || aposta.status === 'EM_ANDAMENTO';
  }

  podeEditar(aposta: Aposta): boolean {
    return aposta.status !== 'FECHADA';
  }

  formatarMoeda(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  dataHojeInput(): string {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
  }

  dataApostaInput(aposta: Aposta): string {
    const dataBase = aposta.dataJogo || aposta.createdAt;

    if (!dataBase) {
      return '';
    }

    const data = new Date(dataBase);

    if (Number.isNaN(data.getTime())) {
      return '';
    }

    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
  }

  adicionarObservacaoDia(): void {
    const texto = this.novaObservacaoDia.trim();

    if (!texto) {
      alert('Digite uma observação.');
      return;
    }

    this.observacoesDia.push(texto);
    this.novaObservacaoDia = '';
  }

  removerObservacaoDia(index: number): void {
    this.observacoesDia.splice(index, 1);
  }

  limparObservacoesDia(): void {
    const confirmar = confirm('Limpar todas as observações do dia?');

    if (!confirmar) {
      return;
    }

    this.observacoesDia = [];
    this.novaObservacaoDia = '';
  }
  dataOperacionalInput(): string {
    if (this.diaOperacional?.data) {
      return this.diaOperacional.data;
    }

    return this.dataHojeInput();
  }

  carregarDiaOperacional(): void {
    this.diaOperacionalService.atual().subscribe({
      next: (dia: DiaOperacional) => {
        this.diaOperacional = dia;
        this.filtros.data = this.dataOperacionalInput();
      },
      error: (erro: any) => {
        console.error('Erro ao carregar dia operacional:', erro);
        this.filtros.data = this.dataHojeInput();
      },
    });
  }
  ionViewWillEnter(): void {
    this.carregarDiaOperacional();
    this.carregar();
  }
}
