import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CatalogueDetailComponent } from './catalogue-detail.component';

describe('Catalogue Management Detail Component', () => {
  let comp: CatalogueDetailComponent;
  let fixture: ComponentFixture<CatalogueDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogueDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./catalogue-detail.component').then(m => m.CatalogueDetailComponent),
              resolve: { catalogue: () => of({ id: 7498 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CatalogueDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CatalogueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load catalogue on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CatalogueDetailComponent);

      // THEN
      expect(instance.catalogue()).toEqual(expect.objectContaining({ id: 7498 }));
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
