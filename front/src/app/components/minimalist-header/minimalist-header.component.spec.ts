import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MinimalistHeaderComponent } from './minimalist-header.component';

describe('MinimalistHeaderComponent', () => {
  let component: MinimalistHeaderComponent;
  let fixture: ComponentFixture<MinimalistHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MinimalistHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MinimalistHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
