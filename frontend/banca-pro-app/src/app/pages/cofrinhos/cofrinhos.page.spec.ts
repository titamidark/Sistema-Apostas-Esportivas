import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CofrinhosPage } from './cofrinhos.page';

describe('CofrinhosPage', () => {
  let component: CofrinhosPage;
  let fixture: ComponentFixture<CofrinhosPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(CofrinhosPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
