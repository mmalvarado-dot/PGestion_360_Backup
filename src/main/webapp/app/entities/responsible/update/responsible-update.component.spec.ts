import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ResponsibleService } from '../service/responsible.service';
import { IResponsible } from '../responsible.model';
import { ResponsibleFormService } from './responsible-form.service';

import { ResponsibleUpdateComponent } from './responsible-update.component';

describe('Responsible Management Update Component', () => {
  let comp: ResponsibleUpdateComponent;
  let fixture: ComponentFixture<ResponsibleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let responsibleFormService: ResponsibleFormService;
  let responsibleService: ResponsibleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ResponsibleUpdateComponent],
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
      .overrideTemplate(ResponsibleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResponsibleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    responsibleFormService = TestBed.inject(ResponsibleFormService);
    responsibleService = TestBed.inject(ResponsibleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const responsible: IResponsible = { id: 5616 };

      activatedRoute.data = of({ responsible });
      comp.ngOnInit();

      expect(comp.responsible).toEqual(responsible);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResponsible>>();
      const responsible = { id: 24783 };
      jest.spyOn(responsibleFormService, 'getResponsible').mockReturnValue(responsible);
      jest.spyOn(responsibleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ responsible });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: responsible }));
      saveSubject.complete();

      // THEN
      expect(responsibleFormService.getResponsible).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(responsibleService.update).toHaveBeenCalledWith(expect.objectContaining(responsible));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResponsible>>();
      const responsible = { id: 24783 };
      jest.spyOn(responsibleFormService, 'getResponsible').mockReturnValue({ id: null });
      jest.spyOn(responsibleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ responsible: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: responsible }));
      saveSubject.complete();

      // THEN
      expect(responsibleFormService.getResponsible).toHaveBeenCalled();
      expect(responsibleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResponsible>>();
      const responsible = { id: 24783 };
      jest.spyOn(responsibleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ responsible });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(responsibleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
