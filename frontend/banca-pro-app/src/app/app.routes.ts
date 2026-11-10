import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.page').then((m) => m.DashboardPage),
  },
  {
    path: 'casas',
    loadComponent: () =>
      import('./pages/casas/casas.page').then((m) => m.CasasPage),
  },
  {
    path: 'bilhetes',
    loadComponent: () =>
      import('./pages/bilhetes/bilhetes.page').then((m) => m.BilhetesPage),
  },
  {
    path: 'caixa',
    loadComponent: () =>
      import('./pages/caixa/caixa.page').then((m) => m.CaixaPage),
  },
  {
    path: 'configuracoes',
    loadComponent: () =>
      import('./pages/configuracoes/configuracoes.page').then(
        (m) => m.ConfiguracoesPage,
      ),
  },

  {
    path: 'cofrinhos',
    loadComponent: () =>
      import('./pages/cofrinhos/cofrinhos.page').then((m) => m.CofrinhosPage),
  },
  {
    path: 'alavancagens',
    loadComponent: () =>
      import('./pages/alavancagens/alavancagens.page').then(
        (m) => m.AlavancagensPage,
      ),
  },

  {
    path: 'alavancagens',
    loadComponent: () =>
      import('./pages/alavancagens/alavancagens.page').then(
        (m) => m.AlavancagensPage,
      ),
  },
];
