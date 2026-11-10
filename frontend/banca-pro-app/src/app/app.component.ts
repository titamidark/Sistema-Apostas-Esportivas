import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

import {
  IonApp,
  IonRouterOutlet,
  IonIcon,
  IonMenu,
  IonContent,
  IonSplitPane,
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    IonApp,
    IonRouterOutlet,
    IonIcon,
    IonMenu,
    IonContent,
    IonSplitPane,
  ],
})
export class AppComponent {
  menuItems = [
    {
      label: 'Dashboard',
      url: '/dashboard',
      icon: 'grid-outline',
    },
    {
      label: 'Casas',
      url: '/casas',
      icon: 'wallet-outline',
    },
    {
      label: 'Bilhetes',
      url: '/bilhetes',
      icon: 'receipt-outline',
    },
    {
      label: 'Caixa',
      url: '/caixa',
      icon: 'cash-outline',
    },
    {
      label: 'Cofrinhos',
      url: '/cofrinhos',
      icon: 'archive-outline',
    },
    {
      label: 'Alavancagens',
      url: '/alavancagens',
      icon: 'trending-up-outline',
    },

    {
      label: 'Configurações',
      url: '/configuracoes',
      icon: 'settings-outline',
    },
  ];
}
