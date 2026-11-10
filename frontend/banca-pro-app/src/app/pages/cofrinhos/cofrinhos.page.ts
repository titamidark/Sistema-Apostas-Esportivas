import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  IonButton,
  IonCard,
  IonCardContent,
  IonContent,
  IonHeader,
  IonInput,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

import {
  Cofrinho,
  CofrinhoDia,
  CofrinhoResumo,
  TipoCofrinho,
} from '../../models/cofrinho';

import { CofrinhoService } from '../../services/cofrinho.service';

@Component({
  selector: 'app-cofrinhos',
  templateUrl: './cofrinhos.page.html',
  styleUrls: ['./cofrinhos.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonCard,
    IonCardContent,
    IonButton,
    IonInput,
  ],
})
export class CofrinhosPage implements OnInit {
  cofrinhos: Cofrinho[] = [];

  diasPorCofrinho: Record<number, CofrinhoDia[]> = {};

  form = {
    nome: '',
    descricao: '',

    valorTotal: 0,
    valorTotalTexto: 'R$ 0,00',

    valorReservadoInicial: 0,
    valorReservadoInicialTexto: 'R$ 0,00',

    dataInicio: '',
    dias: 30,

    tipo: 'META_FIXA' as TipoCofrinho,
  };

  constructor(
    private cofrinhoService: CofrinhoService,
  ) {}

  ngOnInit(): void {
    this.form.dataInicio = this.dataHoje();
    this.carregar();
  }

  carregar(): void {
    this.cofrinhoService.listar().subscribe({
      next: (cofrinhos: Cofrinho[]) => {
        this.cofrinhos = cofrinhos || [];
        this.diasPorCofrinho = {};

        this.cofrinhos.forEach((cofrinho) => {
          this.carregarDias(cofrinho.id);
        });
      },
      error: (erro: any) => {
        console.error(
          'Erro ao carregar cofrinhos:',
          erro,
        );

        alert('Erro ao carregar cofrinhos.');
      },
    });
  }

  carregarDias(cofrinhoId: number): void {
    this.cofrinhoService
      .listarDias(cofrinhoId)
      .subscribe({
        next: (dias: CofrinhoDia[]) => {
          this.diasPorCofrinho[cofrinhoId] =
            dias || [];
        },
        error: (erro: any) => {
          console.error(
            `Erro ao carregar os dias do cofrinho ${cofrinhoId}:`,
            erro,
          );
        },
      });
  }

  criar(): void {
    if (!this.form.nome.trim()) {
      alert('Informe o nome do cofrinho.');
      return;
    }

    if (
      this.form.tipo === 'META_FIXA' &&
      (
        !this.form.valorTotal ||
        this.form.valorTotal <= 0
      )
    ) {
      alert('Informe o valor total.');
      return;
    }

    if (this.form.valorTotal < 0) {
      alert('O valor total não pode ser negativo.');
      return;
    }

    if (this.form.valorReservadoInicial < 0) {
      alert(
        'O valor já reservado não pode ser negativo.',
      );
      return;
    }

    if (
      this.form.valorReservadoInicial >
      this.form.valorTotal
    ) {
      alert(
        'O valor já reservado não pode ser maior que o valor total.',
      );
      return;
    }

    if (
      !this.form.dias ||
      this.form.dias <= 0
    ) {
      alert('Informe a quantidade de dias.');
      return;
    }

    this.cofrinhoService
      .criar({
        nome: this.form.nome.trim(),

        descricao:
          this.form.descricao.trim(),

        valorTotal: Number(
          this.form.valorTotal || 0,
        ),

        valorReservadoInicial: Number(
          this.form.valorReservadoInicial || 0,
        ),

        dataInicio:
          this.normalizarDataParaApi(
            this.form.dataInicio,
          ),

        dias: Number(this.form.dias),

        tipo: this.form.tipo,
      })
      .subscribe({
        next: () => {
          this.limparFormulario();
          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao criar cofrinho:',
            erro,
          );

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao criar cofrinho.';

          alert(mensagem);
        },
      });
  }

  limparFormulario(): void {
    this.form = {
      nome: '',
      descricao: '',

      valorTotal: 0,
      valorTotalTexto: 'R$ 0,00',

      valorReservadoInicial: 0,
      valorReservadoInicialTexto:
        'R$ 0,00',

      dataInicio: this.dataHoje(),
      dias: 30,

      tipo: 'META_FIXA' as TipoCofrinho,
    };
  }

  marcarGreen(
    cofrinho: Cofrinho,
    dia: CofrinhoDia,
  ): void {
    const digitado = prompt(
      'Valor depositado:',
      this.numeroInput(dia.valorPrevisto),
    );

    if (digitado === null) {
      return;
    }

    const valor =
      this.converterMoedaParaNumero(digitado);

    if (!valor || valor <= 0) {
      alert('Informe um valor válido.');
      return;
    }

    this.cofrinhoService
      .marcarDia(
        cofrinho.id,
        dia.id,
        'GREEN',
        valor,
      )
      .subscribe({
        next: () => {
          /*
           * É necessário buscar novamente os dias
           * para verificar se o último pagamento
           * fechou o cofrinho.
           */
          this.cofrinhoService
            .listarDias(cofrinho.id)
            .subscribe({
              next: (
                diasAtualizados: CofrinhoDia[],
              ) => {
                this.diasPorCofrinho[
                  cofrinho.id
                ] = diasAtualizados || [];

                if (
                  this.cofrinhoFechado(cofrinho)
                ) {
                  this.oferecerRenovacao(
                    cofrinho,
                  );
                } else {
                  this.carregar();
                }
              },
              error: (erro: any) => {
                console.error(
                  'Erro ao verificar o fechamento do cofrinho:',
                  erro,
                );

                this.carregar();
              },
            });
        },
        error: (erro: any) => {
          console.error(
            'Erro ao marcar como depositado:',
            erro,
          );

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao marcar como depositado.';

          alert(mensagem);
        },
      });
  }

  marcarRed(
    cofrinho: Cofrinho,
    dia: CofrinhoDia,
  ): void {
    const confirmar = confirm(
      'Marcar este dia como NÃO depositado?\n\n' +
        'O valor será redistribuído nos próximos dias.',
    );

    if (!confirmar) {
      return;
    }

    this.cofrinhoService
      .marcarDia(
        cofrinho.id,
        dia.id,
        'RED',
        0,
      )
      .subscribe({
        next: () => {
          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao marcar como não depositado:',
            erro,
          );

          alert(
            'Erro ao marcar como não depositado.',
          );
        },
      });
  }

  voltarPendente(
    cofrinho: Cofrinho,
    dia: CofrinhoDia,
  ): void {
    this.cofrinhoService
      .marcarDia(
        cofrinho.id,
        dia.id,
        'PENDENTE',
        0,
      )
      .subscribe({
        next: () => {
          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao voltar para pendente:',
            erro,
          );

          alert(
            'Erro ao voltar para pendente.',
          );
        },
      });
  }

  excluir(id: number): void {
    const confirmar = confirm(
      'Deseja excluir este cofrinho e todos os seus dias?',
    );

    if (!confirmar) {
      return;
    }

    this.cofrinhoService
      .deletar(id)
      .subscribe({
        next: () => {
          delete this.diasPorCofrinho[id];
          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao excluir cofrinho:',
            erro,
          );

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao excluir cofrinho.';

          alert(mensagem);
        },
      });
  }

  resumo(
    cofrinhoId: number,
  ): CofrinhoResumo {
    const dias =
      this.diasPorCofrinho[cofrinhoId] || [];

    const cofrinho =
      this.cofrinhos.find(
        (item) => item.id === cofrinhoId,
      );

    const meta =
      this.arredondarMoeda(
        Number(cofrinho?.valorTotal || 0),
      );

    const reservaInicial =
      this.arredondarMoeda(
        Number(
          cofrinho?.valorReservadoInicial || 0,
        ),
      );

    const depositadoPeriodo =
      this.arredondarMoeda(
        dias
          .filter(
            (dia) =>
              dia.status === 'GREEN',
          )
          .reduce(
            (total, dia) =>
              total +
              Number(
                dia.valorDepositado || 0,
              ),
            0,
          ),
      );

    const totalReservado =
      this.arredondarMoeda(
        reservaInicial +
          depositadoPeriodo,
      );

    const pendentes =
      dias.filter(
        (dia) =>
          dia.status === 'PENDENTE',
      ).length;

    const verdes =
      dias.filter(
        (dia) => dia.status === 'GREEN',
      ).length;

    const vermelhos =
      dias.filter(
        (dia) => dia.status === 'RED',
      ).length;

    const falta =
      this.arredondarMoeda(
        Math.max(
          0,
          meta - totalReservado,
        ),
      );

    const valorDiarioAtual =
      pendentes > 0
        ? this.arredondarMoeda(
            falta / pendentes,
          )
        : 0;

    const progresso =
      meta > 0
        ? Math.min(
            100,
            (
              totalReservado /
              meta
            ) * 100,
          )
        : 0;

    return {
      reservaInicial,
      depositadoPeriodo,

      /*
       * Mantemos a propriedade depositado
       * representando o total reservado para
       * preservar o uso atual no HTML.
       */
      depositado: totalReservado,

      falta,
      pendentes,
      verdes,
      vermelhos,
      progresso,
      valorDiarioAtual,
    };
  }

  cofrinhoFechado(
    cofrinho: Cofrinho,
  ): boolean {
    const dias =
      this.diasPorCofrinho[
        cofrinho.id
      ] || [];

    if (dias.length === 0) {
      return false;
    }

    const resumo =
      this.resumo(cofrinho.id);

    /*
     * O cofrinho fecha quando:
     *
     * 1. nenhum dia estiver pendente;
     * 2. a meta tiver sido totalmente reservada.
     *
     * Dias RED podem existir, desde que o valor
     * tenha sido redistribuído e a meta tenha
     * sido atingida nos demais dias.
     */
    const todosDiasResolvidos =
      dias.every(
        (dia) =>
          dia.status !== 'PENDENTE',
      );

    const metaAtingida =
      resumo.falta <= 0.01;

    return (
      todosDiasResolvidos &&
      metaAtingida
    );
  }

  cofrinhosAbertos(): Cofrinho[] {
    return [...this.cofrinhos]
      .filter(
        (cofrinho) =>
          !this.cofrinhoFechado(
            cofrinho,
          ),
      )
      .sort(
        (a, b) =>
          this.dataEmMilissegundos(
            a.dataFinal,
          ) -
          this.dataEmMilissegundos(
            b.dataFinal,
          ),
      );
  }

  cofrinhosFechados(): Cofrinho[] {
    return [...this.cofrinhos]
      .filter(
        (cofrinho) =>
          this.cofrinhoFechado(
            cofrinho,
          ),
      )
      .sort(
        (a, b) =>
          this.dataEmMilissegundos(
            b.dataFinal,
          ) -
          this.dataEmMilissegundos(
            a.dataFinal,
          ),
      );
  }

  dataEmMilissegundos(
    data?: string,
  ): number {
    if (!data) {
      return 0;
    }

    return new Date(
      `${data}T00:00:00`,
    ).getTime();
  }

  alterarDiasRestantes(
    cofrinho: Cofrinho,
  ): void {
    const digitado = prompt(
      'Informe o novo número de dias restantes:',
      String(
        this.resumo(
          cofrinho.id,
        ).pendentes,
      ),
    );

    if (digitado === null) {
      return;
    }

    const diasRestantes =
      Number(digitado);

    if (
      !diasRestantes ||
      diasRestantes <= 0
    ) {
      alert(
        'Informe uma quantidade válida.',
      );
      return;
    }

    this.cofrinhoService
      .alterarDiasRestantes(
        cofrinho.id,
        diasRestantes,
      )
      .subscribe({
        next: () => {
          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao alterar dias restantes:',
            erro,
          );

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao alterar dias restantes.';

          alert(mensagem);
        },
      });
  }

  adicionarGasto(
    cofrinho: Cofrinho,
  ): void {
    if (
      cofrinho.tipo !==
      'VALOR_VARIAVEL'
    ) {
      alert(
        'Este cofrinho não aceita gastos variáveis.',
      );
      return;
    }

    const digitado = prompt(
      `Valor do gasto para ${cofrinho.nome}:`,
      '0,00',
    );

    if (digitado === null) {
      return;
    }

    const valor =
      this.converterMoedaParaNumero(
        digitado,
      );

    if (!valor || valor <= 0) {
      alert('Informe um valor válido.');
      return;
    }

    const descricao =
      prompt(
        'Descrição do gasto:',
        '',
      ) || '';

    this.cofrinhoService
      .adicionarGasto(
        cofrinho.id,
        valor,
        descricao.trim(),
      )
      .subscribe({
        next: () => {
          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao adicionar gasto:',
            erro,
          );

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao adicionar gasto.';

          alert(mensagem);
        },
      });
  }

  statusCofrinho(
    cofrinho: Cofrinho,
  ): string {
    const dias =
      this.diasPorCofrinho[
        cofrinho.id
      ] || [];

    if (!dias.length) {
      return 'Sem dias';
    }

    if (
      this.cofrinhoFechado(cofrinho)
    ) {
      return 'Fechado';
    }

    const hoje =
      this.dataHoje();

    if (
      cofrinho.dataInicio &&
      hoje < cofrinho.dataInicio
    ) {
      return (
        `Começa em ` +
        this.formatarData(
          cofrinho.dataInicio,
        )
      );
    }

    const pendentesVencidos =
      dias.filter(
        (dia) =>
          dia.data < hoje &&
          dia.status === 'PENDENTE',
      ).length;

    if (
      pendentesVencidos > 0
    ) {
      return (
        `Atrasado ` +
        `${pendentesVencidos} dia(s)`
      );
    }

    const redsVencidos =
      dias.filter(
        (dia) =>
          dia.data < hoje &&
          dia.status === 'RED',
      ).length;

    if (redsVencidos > 0) {
      return (
        `Remanejado ` +
        `${redsVencidos} dia(s)`
      );
    }

    const pagosFuturos =
      dias.filter(
        (dia) =>
          dia.data > hoje &&
          dia.status === 'GREEN',
      ).length;

    if (pagosFuturos > 0) {
      return (
        `Adiantado ` +
        `${pagosFuturos} dia(s)`
      );
    }

    const diaHoje =
      dias.find(
        (dia) =>
          dia.data === hoje,
      );

    if (
      diaHoje?.status === 'GREEN'
    ) {
      return 'Pago hoje';
    }

    if (
      diaHoje?.status === 'RED'
    ) {
      return 'Remanejado hoje';
    }

    if (
      this.diasParaVencer(
        cofrinho,
      ) < 0
    ) {
      return (
        `Vencido há ` +
        `${Math.abs(
          this.diasParaVencer(
            cofrinho,
          ),
        )} dia(s)`
      );
    }

    return 'Em dia';
  }

  classeStatusCofrinho(
    cofrinho: Cofrinho,
  ): string {
    const status =
      this.statusCofrinho(
        cofrinho,
      );

    if (
      status.includes('Remanejado') ||
      status.includes('Atrasado') ||
      status.includes('Vencido')
    ) {
      return 'status-red';
    }

    if (
      status.includes('Adiantado')
    ) {
      return 'status-blue';
    }

    if (
      status.includes('Em dia') ||
      status.includes('Pago hoje') ||
      status.includes('Fechado')
    ) {
      return 'status-green';
    }

    return 'status-neutral';
  }

  valorDiarioCofrinho(
    cofrinho: Cofrinho,
  ): number {
    return this.resumo(
      cofrinho.id,
    ).valorDiarioAtual;
  }

  depositoDiarioPrevisto(): number {
    const total =
      this.cofrinhosAbertos()
        .reduce(
          (
            acumulado,
            cofrinho,
          ) => {
            const resumo =
              this.resumo(
                cofrinho.id,
              );

            if (
              resumo.pendentes <= 0
            ) {
              return acumulado;
            }

            return (
              acumulado +
              Number(
                resumo.valorDiarioAtual ||
                  0,
              )
            );
          },
          0,
        );

    return this.arredondarMoeda(
      total,
    );
  }

  saldoReservado(): number {
    const total =
      this.cofrinhosAbertos()
        .reduce(
          (
            acumulado,
            cofrinho,
          ) => {
            const resumo =
              this.resumo(
                cofrinho.id,
              );

            return (
              acumulado +
              Number(
                resumo.depositado || 0,
              )
            );
          },
          0,
        );

    return this.arredondarMoeda(
      total,
    );
  }

  diasParaVencer(
    cofrinho: Cofrinho,
  ): number {
    if (!cofrinho.dataFinal) {
      return 0;
    }

    const hoje = new Date(
      `${this.dataHoje()}T00:00:00`,
    );

    const fim = new Date(
      `${cofrinho.dataFinal}T00:00:00`,
    );

    const diferenca =
      fim.getTime() -
      hoje.getTime();

    return Math.round(
      diferenca /
        (
          1000 *
          60 *
          60 *
          24
        ),
    );
  }

  onValorTotalInput(
    event: any,
  ): void {
    const texto =
      event.detail.value || '';

    const numeros =
      texto.replace(/\D/g, '');

    const valor =
      Number(numeros) / 100;

    this.form.valorTotal =
      valor;

    this.form.valorTotalTexto =
      this.moeda(valor);
  }

  onValorReservadoInicialInput(
    event: any,
  ): void {
    const texto =
      event.detail.value || '';

    const numeros =
      texto.replace(/\D/g, '');

    const valor =
      Number(numeros) / 100;

    this.form.valorReservadoInicial =
      valor;

    this.form.valorReservadoInicialTexto =
      this.moeda(valor);
  }

  arredondarMoeda(
    valor: number,
  ): number {
    return (
      Math.round(
        (
          Number(valor || 0) +
          Number.EPSILON
        ) * 100,
      ) / 100
    );
  }

  dataHoje(): string {
    const hoje =
      new Date();

    const ano =
      hoje.getFullYear();

    const mes =
      String(
        hoje.getMonth() + 1,
      ).padStart(2, '0');

    const dia =
      String(
        hoje.getDate(),
      ).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
  }

  moeda(valor: number): string {
    return Number(
      valor || 0,
    ).toLocaleString(
      'pt-BR',
      {
        style: 'currency',
        currency: 'BRL',
      },
    );
  }

  numeroInput(
    valor: number,
  ): string {
    return Number(
      valor || 0,
    ).toLocaleString(
      'pt-BR',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      },
    );
  }

  converterMoedaParaNumero(
    valor: string,
  ): number {
    if (!valor) {
      return 0;
    }

    let texto =
      valor
        .replace('R$', '')
        .replace(/\s/g, '')
        .trim();

    if (
      texto.includes(',') &&
      texto.includes('.')
    ) {
      texto =
        texto
          .replace(/\./g, '')
          .replace(',', '.');
    } else if (
      texto.includes(',')
    ) {
      texto =
        texto.replace(',', '.');
    }

    const convertido =
      Number(texto);

    return Number.isFinite(
      convertido,
    )
      ? convertido
      : 0;
  }

  formatarData(
    data: string,
  ): string {
    if (!data) {
      return '';
    }

    const partes =
      data.split('-');

    if (
      partes.length !== 3
    ) {
      return data;
    }

    return (
      `${partes[2]}/` +
      `${partes[1]}/` +
      `${partes[0]}`
    );
  }

  normalizarDataParaApi(
    data: string,
  ): string {
    if (!data) {
      return this.dataHoje();
    }

    if (data.includes('/')) {
      const partes =
        data.split('/');

      if (
        partes.length === 3
      ) {
        return (
          `${partes[2]}-` +
          `${partes[1]}-` +
          `${partes[0]}`
        );
      }
    }

    return data;
  }

  calcularDataFinal(
    cofrinho: Cofrinho,
  ): string {
    if (
      !cofrinho.dataInicio ||
      !cofrinho.dias
    ) {
      return '';
    }

    const data =
      new Date(
        `${cofrinho.dataInicio}T00:00:00`,
      );

    data.setDate(
      data.getDate() +
        Number(cofrinho.dias) -
        1,
    );

    const ano =
      data.getFullYear();

    const mes =
      String(
        data.getMonth() + 1,
      ).padStart(2, '0');

    const dia =
      String(
        data.getDate(),
      ).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
  }

  private oferecerRenovacao(
    cofrinho: Cofrinho,
  ): void {
    /*
     * Atualiza a tela primeiro para o cofrinho
     * aparecer na seção de fechados.
     */
    this.carregar();

    const repetir = confirm(
      `O cofrinho "${cofrinho.nome}" foi concluído.\n\n` +
        'Deseja repetir por mais 30 dias?',
    );

    if (!repetir) {
      return;
    }

    const valorDigitado = prompt(
      'Informe o valor total do novo período:',
      this.numeroInput(
        cofrinho.valorTotal,
      ),
    );

    if (
      valorDigitado === null
    ) {
      return;
    }

    const novoValor =
      this.converterMoedaParaNumero(
        valorDigitado,
      );

    if (
      cofrinho.tipo ===
        'META_FIXA' &&
      (
        !novoValor ||
        novoValor <= 0
      )
    ) {
      alert(
        'Informe um valor válido.',
      );
      return;
    }

    const reservaDigitada =
      prompt(
        'Quanto você já possui reservado para o novo período?',
        '0,00',
      );

    if (
      reservaDigitada === null
    ) {
      return;
    }

    const novaReservaInicial =
      this.converterMoedaParaNumero(
        reservaDigitada,
      );

    if (
      novaReservaInicial < 0
    ) {
      alert(
        'A reserva inicial não pode ser negativa.',
      );
      return;
    }

    if (
      novaReservaInicial >
      novoValor
    ) {
      alert(
        'A reserva inicial não pode ser maior que o novo valor total.',
      );
      return;
    }

    const hoje =
      this.dataHoje();

    this.cofrinhoService
      .criar({
        nome: cofrinho.nome,

        descricao:
          cofrinho.descricao || '',

        valorTotal:
          cofrinho.tipo ===
          'VALOR_VARIAVEL'
            ? Number(
                novoValor || 0,
              )
            : novoValor,

        valorReservadoInicial:
          novaReservaInicial,

        dataInicio: hoje,

        dias: 30,

        tipo:
          cofrinho.tipo ||
          'META_FIXA',
      })
      .subscribe({
        next: () => {
          alert(
            'Novo período de 30 dias criado com sucesso.',
          );

          this.carregar();
        },
        error: (erro: any) => {
          console.error(
            'Erro ao renovar cofrinho:',
            erro,
          );

          const mensagem =
            erro?.error?.message ||
            erro?.error ||
            'Erro ao renovar o cofrinho.';

          alert(mensagem);
        },
      });
  }
}