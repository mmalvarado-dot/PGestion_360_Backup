import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICatalogue } from 'app/entities/catalogue/catalogue.model';
import { CatalogueService } from 'app/entities/catalogue/service/catalogue.service';
import { ItemCatalogueService } from '../service/item-catalogue.service';
import { IItemCatalogue } from '../item-catalogue.model';
import { ItemCatalogueFormService } from './item-catalogue-form.service';

import { ItemCatalogueUpdateComponent } from './item-catalogue-update.component';

describe('ItemCatalogue Management Update Component', () => {
  let comp: ItemCatalogueUpdateComponent;
  let fixture: ComponentFixture<ItemCatalogueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let itemCatalogueFormService: ItemCatalogueFormService;
  let itemCatalogueService: ItemCatalogueService;
  let catalogueService: CatalogueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ItemCatalogueUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ItemCatalogueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ItemCatalogueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    itemCatalogueFormService = TestBed.inject(ItemCatalogueFormService);
    itemCatalogueService = TestBed.inject(ItemCatalogueService);
    catalogueService = TestBed.inject(CatalogueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Catalogue query and add missing value', () => {
      const itemCatalogue: IItemCatalogue = { id: 3864 };
      const catalogue: ICatalogue = { id: 7498 };
      itemCatalogue.catalogue = catalogue;

      const catalogueCollection: ICatalogue[] = [{ id: 7498 }];
      jest.spyOn(catalogueService, 'query').mockReturnValue(of(new HttpResponse({ body: catalogueCollection })));
      const additionalCatalogues = [catalogue];
      const expectedCollection: ICatalogue[] = [...additionalCatalogues, ...catalogueCollection];
      jest.spyOn(catalogueService, 'addCatalogueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ itemCatalogue });
      comp.ngOnInit();

      expect(catalogueService.query).toHaveBeenCalled();
      expect(catalogueService.addCatalogueToCollectionIfMissing).toHaveBeenCalledWith(
        catalogueCollection,
        ...additionalCatalogues.map(expect.objectContaining),
      );
      expect(comp.cataloguesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const itemCatalogue: IItemCatalogue = { id: 3864 };
      const catalogue: ICatalogue = { id: 7498 };
      itemCatalogue.catalogue = catalogue;

      activatedRoute.data = of({ itemCatalogue });
      comp.ngOnInit();

      expect(comp.cataloguesSharedCollection).toContainEqual(catalogue);
      expect(comp.itemCatalogue).toEqual(itemCatalogue);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IItemCatalogue>>();
      const itemCatalogue = { id: 24609 };
      jest.spyOn(itemCatalogueFormService, 'getItemCatalogue').mockReturnValue(itemCatalogue);
      jest.spyOn(itemCatalogueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ itemCatalogue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: itemCatalogue }));
      saveSubject.complete();

      // THEN
      expect(itemCatalogueFormService.getItemCatalogue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(itemCatalogueService.update).toHaveBeenCalledWith(expect.objectContaining(itemCatalogue));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IItemCatalogue>>();
      const itemCatalogue = { id: 24609 };
      jest.spyOn(itemCatalogueFormService, 'getItemCatalogue').mockReturnValue({ id: null });
      jest.spyOn(itemCatalogueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ itemCatalogue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: itemCatalogue }));
      saveSubject.complete();

      // THEN
      expect(itemCatalogueFormService.getItemCatalogue).toHaveBeenCalled();
      expect(itemCatalogueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IItemCatalogue>>();
      const itemCatalogue = { id: 24609 };
      jest.spyOn(itemCatalogueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ itemCatalogue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(itemCatalogueService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCatalogue', () => {
      it('should forward to catalogueService', () => {
        const entity = { id: 7498 };
        const entity2 = { id: 28253 };
        jest.spyOn(catalogueService, 'compareCatalogue');
        comp.compareCatalogue(entity, entity2);
        expect(catalogueService.compareCatalogue).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
