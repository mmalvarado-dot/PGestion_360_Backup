import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IResponsible } from 'app/entities/responsible/responsible.model';
import { ResponsibleService } from 'app/entities/responsible/service/responsible.service';
import { IItemCatalogue } from 'app/entities/item-catalogue/item-catalogue.model';
import { ItemCatalogueService } from 'app/entities/item-catalogue/service/item-catalogue.service';
import { IChangeRequest } from '../change-request.model';
import { ChangeRequestService } from '../service/change-request.service';
import { ChangeRequestFormService } from './change-request-form.service';

import { ChangeRequestUpdateComponent } from './change-request-update.component';

describe('ChangeRequest Management Update Component', () => {
  let comp: ChangeRequestUpdateComponent;
  let fixture: ComponentFixture<ChangeRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let changeRequestFormService: ChangeRequestFormService;
  let changeRequestService: ChangeRequestService;
  let responsibleService: ResponsibleService;
  let itemCatalogueService: ItemCatalogueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ChangeRequestUpdateComponent],
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
      .overrideTemplate(ChangeRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChangeRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    changeRequestFormService = TestBed.inject(ChangeRequestFormService);
    changeRequestService = TestBed.inject(ChangeRequestService);
    responsibleService = TestBed.inject(ResponsibleService);
    itemCatalogueService = TestBed.inject(ItemCatalogueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Responsible query and add missing value', () => {
      const changeRequest: IChangeRequest = { id: 20589 };
      const responsible: IResponsible = { id: 24783 };
      changeRequest.responsible = responsible;

      const responsibleCollection: IResponsible[] = [{ id: 24783 }];
      jest.spyOn(responsibleService, 'query').mockReturnValue(of(new HttpResponse({ body: responsibleCollection })));
      const additionalResponsibles = [responsible];
      const expectedCollection: IResponsible[] = [...additionalResponsibles, ...responsibleCollection];
      jest.spyOn(responsibleService, 'addResponsibleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ changeRequest });
      comp.ngOnInit();

      expect(responsibleService.query).toHaveBeenCalled();
      expect(responsibleService.addResponsibleToCollectionIfMissing).toHaveBeenCalledWith(
        responsibleCollection,
        ...additionalResponsibles.map(expect.objectContaining),
      );
      expect(comp.responsiblesSharedCollection).toEqual(expectedCollection);
    });

    it('should call ItemCatalogue query and add missing value', () => {
      const changeRequest: IChangeRequest = { id: 20589 };
      const itemCatalogue: IItemCatalogue = { id: 24609 };
      changeRequest.itemCatalogue = itemCatalogue;

      const itemCatalogueCollection: IItemCatalogue[] = [{ id: 24609 }];
      jest.spyOn(itemCatalogueService, 'query').mockReturnValue(of(new HttpResponse({ body: itemCatalogueCollection })));
      const additionalItemCatalogues = [itemCatalogue];
      const expectedCollection: IItemCatalogue[] = [...additionalItemCatalogues, ...itemCatalogueCollection];
      jest.spyOn(itemCatalogueService, 'addItemCatalogueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ changeRequest });
      comp.ngOnInit();

      expect(itemCatalogueService.query).toHaveBeenCalled();
      expect(itemCatalogueService.addItemCatalogueToCollectionIfMissing).toHaveBeenCalledWith(
        itemCatalogueCollection,
        ...additionalItemCatalogues.map(expect.objectContaining),
      );
      expect(comp.itemCataloguesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const changeRequest: IChangeRequest = { id: 20589 };
      const responsible: IResponsible = { id: 24783 };
      changeRequest.responsible = responsible;
      const itemCatalogue: IItemCatalogue = { id: 24609 };
      changeRequest.itemCatalogue = itemCatalogue;

      activatedRoute.data = of({ changeRequest });
      comp.ngOnInit();

      expect(comp.responsiblesSharedCollection).toContainEqual(responsible);
      expect(comp.itemCataloguesSharedCollection).toContainEqual(itemCatalogue);
      expect(comp.changeRequest).toEqual(changeRequest);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChangeRequest>>();
      const changeRequest = { id: 26371 };
      jest.spyOn(changeRequestFormService, 'getChangeRequest').mockReturnValue(changeRequest);
      jest.spyOn(changeRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ changeRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: changeRequest }));
      saveSubject.complete();

      // THEN
      expect(changeRequestFormService.getChangeRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(changeRequestService.update).toHaveBeenCalledWith(expect.objectContaining(changeRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChangeRequest>>();
      const changeRequest = { id: 26371 };
      jest.spyOn(changeRequestFormService, 'getChangeRequest').mockReturnValue({ id: null });
      jest.spyOn(changeRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ changeRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: changeRequest }));
      saveSubject.complete();

      // THEN
      expect(changeRequestFormService.getChangeRequest).toHaveBeenCalled();
      expect(changeRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChangeRequest>>();
      const changeRequest = { id: 26371 };
      jest.spyOn(changeRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ changeRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(changeRequestService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareResponsible', () => {
      it('should forward to responsibleService', () => {
        const entity = { id: 24783 };
        const entity2 = { id: 5616 };
        jest.spyOn(responsibleService, 'compareResponsible');
        comp.compareResponsible(entity, entity2);
        expect(responsibleService.compareResponsible).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareItemCatalogue', () => {
      it('should forward to itemCatalogueService', () => {
        const entity = { id: 24609 };
        const entity2 = { id: 3864 };
        jest.spyOn(itemCatalogueService, 'compareItemCatalogue');
        comp.compareItemCatalogue(entity, entity2);
        expect(itemCatalogueService.compareItemCatalogue).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
