import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ResponsibleDetailComponent } from './responsible-detail.component';

describe('Responsible Management Detail Component', () => {
  let comp: ResponsibleDetailComponent;
  let fixture: ComponentFixture<ResponsibleDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsibleDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./responsible-detail.component').then(m => m.ResponsibleDetailComponent),
              resolve: { responsible: () => of({ id: 24783 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ResponsibleDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResponsibleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load responsible on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ResponsibleDetailComponent);

      // THEN
      expect(instance.responsible()).toEqual(expect.objectContaining({ id: 24783 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
