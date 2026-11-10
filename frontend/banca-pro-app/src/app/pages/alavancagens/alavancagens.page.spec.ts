import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlavancagensPage } from './alavancagens.page';

describe('AlavancagensPage', () => {
  let component: AlavancagensPage;
  let fixture: ComponentFixture<AlavancagensPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(AlavancagensPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
