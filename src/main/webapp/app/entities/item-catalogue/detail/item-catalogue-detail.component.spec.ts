import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ItemCatalogueDetailComponent } from './item-catalogue-detail.component';

describe('ItemCatalogue Management Detail Component', () => {
  let comp: ItemCatalogueDetailComponent;
  let fixture: ComponentFixture<ItemCatalogueDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemCatalogueDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./item-catalogue-detail.component').then(m => m.ItemCatalogueDetailComponent),
              resolve: { itemCatalogue: () => of({ id: 24609 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ItemCatalogueDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemCatalogueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load itemCatalogue on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ItemCatalogueDetailComponent);

      // THEN
      expect(instance.itemCatalogue()).toEqual(expect.objectContaining({ id: 24609 }));
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
