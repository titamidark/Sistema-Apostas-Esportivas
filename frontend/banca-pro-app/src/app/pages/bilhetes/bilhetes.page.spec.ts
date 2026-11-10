import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BilhetesPage } from './bilhetes.page';

describe('BilhetesPage', () => {
  let component: BilhetesPage;
  let fixture: ComponentFixture<BilhetesPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(BilhetesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
