import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Allstocks } from './allstocks';

describe('Allstocks', () => {
  let component: Allstocks;
  let fixture: ComponentFixture<Allstocks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Allstocks]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Allstocks);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
