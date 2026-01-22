import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { CatalogueService } from '../service/catalogue.service';
import { ICatalogue } from '../catalogue.model';
import { CatalogueFormService } from './catalogue-form.service';

import { CatalogueUpdateComponent } from './catalogue-update.component';

describe('Catalogue Management Update Component', () => {
  let comp: CatalogueUpdateComponent;
  let fixture: ComponentFixture<CatalogueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let catalogueFormService: CatalogueFormService;
  let catalogueService: CatalogueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CatalogueUpdateComponent],
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
      .overrideTemplate(CatalogueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CatalogueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    catalogueFormService = TestBed.inject(CatalogueFormService);
    catalogueService = TestBed.inject(CatalogueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const catalogue: ICatalogue = { id: 28253 };

      activatedRoute.data = of({ catalogue });
      comp.ngOnInit();

      expect(comp.catalogue).toEqual(catalogue);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICatalogue>>();
      const catalogue = { id: 7498 };
      jest.spyOn(catalogueFormService, 'getCatalogue').mockReturnValue(catalogue);
      jest.spyOn(catalogueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ catalogue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: catalogue }));
      saveSubject.complete();

      // THEN
      expect(catalogueFormService.getCatalogue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(catalogueService.update).toHaveBeenCalledWith(expect.objectContaining(catalogue));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICatalogue>>();
      const catalogue = { id: 7498 };
      jest.spyOn(catalogueFormService, 'getCatalogue').mockReturnValue({ id: null });
      jest.spyOn(catalogueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ catalogue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: catalogue }));
      saveSubject.complete();

      // THEN
      expect(catalogueFormService.getCatalogue).toHaveBeenCalled();
      expect(catalogueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICatalogue>>();
      const catalogue = { id: 7498 };
      jest.spyOn(catalogueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ catalogue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(catalogueService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
