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

import { CicloAlavancagem, ProjetoAlavancagem } from '../../models/alavancagem';

import { AlavancagemService } from '../../services/alavancagem.service';

@Component({
  selector: 'app-alavancagens',
  templateUrl: './alavancagens.page.html',
  styleUrls: ['./alavancagens.page.scss'],
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
export class AlavancagensPage implements OnInit {
  projetos: ProjetoAlavancagem[] = [];
  ciclosPorProjeto: Record<number, CicloAlavancagem[]> = {};

  form = {
    nome: '',
    descricao: '',
    valorInicial: 0,
    valorInicialTexto: 'R$ 0,00',
    rodadasPorCiclo: 3,
    percentualReinvestimento: 100,
  };

  constructor(private alavancagemService: AlavancagemService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.alavancagemService.listar().subscribe({
      next: (dados: ProjetoAlavancagem[]) => {
        this.projetos = dados || [];

        this.projetos.forEach((projeto) => {
          this.carregarCiclos(projeto.id);
        });
      },
      error: (erro: any) => {
        console.error(erro);
        alert('Erro ao carregar alavancagens.');
      },
    });
  }

  carregarCiclos(projetoId: number): void {
    this.alavancagemService.listarCiclos(projetoId).subscribe({
      next: (ciclos: CicloAlavancagem[]) => {
        this.ciclosPorProjeto[projetoId] = ciclos || [];
      },
      error: (erro: any) => console.error(erro),
    });
  }

  criar(): void {
    if (!this.form.nome.trim()) {
      alert('Informe o nome do projeto.');
      return;
    }

    if (!this.form.valorInicial || this.form.valorInicial <= 0) {
      alert('Informe o valor inicial.');
      return;
    }

    if (!this.form.rodadasPorCiclo || this.form.rodadasPorCiclo <= 0) {
      alert('Informe a quantidade de rodadas por ciclo.');
      return;
    }

    if (
      this.form.percentualReinvestimento < 0 ||
      this.form.percentualReinvestimento > 100
    ) {
      alert('O percentual de reinvestimento deve estar entre 0 e 100.');
      return;
    }

    this.alavancagemService
      .criar({
        nome: this.form.nome.trim(),
        descricao: this.form.descricao,
        valorInicial: Number(this.form.valorInicial),
        rodadasPorCiclo: Number(this.form.rodadasPorCiclo),
        percentualReinvestimento: Number(this.form.percentualReinvestimento),
      })
      .subscribe({
        next: () => {
          this.limpar();
          this.carregar();
        },
        error: (erro: any) => {
          console.error(erro);

          const mensagem =
            erro?.error?.message || erro?.error || 'Erro ao criar projeto.';

          alert(mensagem);
        },
      });
  }

  limpar(): void {
    this.form = {
      nome: '',
      descricao: '',
      valorInicial: 0,
      valorInicialTexto: 'R$ 0,00',
      rodadasPorCiclo: 3,
      percentualReinvestimento: 100,
    };
  }

  onValorInicialInput(event: any): void {
    const texto = event.detail.value || '';
    const numeros = texto.replace(/\D/g, '');
    const valor = Number(numeros) / 100;

    this.form.valorInicial = valor;
    this.form.valorInicialTexto = this.moeda(valor);
  }

  moeda(valor: number): string {
    return Number(valor || 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}
