import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MusicianApplicationComponent } from './musician-application.component';

describe('MusicianApplicationComponent', () => {
  let component: MusicianApplicationComponent;
  let fixture: ComponentFixture<MusicianApplicationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MusicianApplicationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MusicianApplicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
