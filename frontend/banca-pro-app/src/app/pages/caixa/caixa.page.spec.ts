import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CaixaPage } from './caixa.page';

describe('CaixaPage', () => {
  let component: CaixaPage;
  let fixture: ComponentFixture<CaixaPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(CaixaPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
