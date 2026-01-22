import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DepartmentService } from '../service/department.service';
import { IDepartment } from '../department.model';
import { DepartmentFormService } from './department-form.service';

import { DepartmentUpdateComponent } from './department-update.component';

describe('Department Management Update Component', () => {
  let comp: DepartmentUpdateComponent;
  let fixture: ComponentFixture<DepartmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let departmentFormService: DepartmentFormService;
  let departmentService: DepartmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DepartmentUpdateComponent],
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
      .overrideTemplate(DepartmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DepartmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    departmentFormService = TestBed.inject(DepartmentFormService);
    departmentService = TestBed.inject(DepartmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Department query and add missing value', () => {
      const department: IDepartment = { id: 15970 };
      const parentDepartment: IDepartment = { id: 29518 };
      department.parentDepartment = parentDepartment;

      const departmentCollection: IDepartment[] = [{ id: 29518 }];
      jest.spyOn(departmentService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentCollection })));
      const additionalDepartments = [parentDepartment];
      const expectedCollection: IDepartment[] = [...additionalDepartments, ...departmentCollection];
      jest.spyOn(departmentService, 'addDepartmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ department });
      comp.ngOnInit();

      expect(departmentService.query).toHaveBeenCalled();
      expect(departmentService.addDepartmentToCollectionIfMissing).toHaveBeenCalledWith(
        departmentCollection,
        ...additionalDepartments.map(expect.objectContaining),
      );
      expect(comp.departmentsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const department: IDepartment = { id: 15970 };
      const parentDepartment: IDepartment = { id: 29518 };
      department.parentDepartment = parentDepartment;

      activatedRoute.data = of({ department });
      comp.ngOnInit();

      expect(comp.departmentsSharedCollection).toContainEqual(parentDepartment);
      expect(comp.department).toEqual(department);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDepartment>>();
      const department = { id: 29518 };
      jest.spyOn(departmentFormService, 'getDepartment').mockReturnValue(department);
      jest.spyOn(departmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ department });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: department }));
      saveSubject.complete();

      // THEN
      expect(departmentFormService.getDepartment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(departmentService.update).toHaveBeenCalledWith(expect.objectContaining(department));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDepartment>>();
      const department = { id: 29518 };
      jest.spyOn(departmentFormService, 'getDepartment').mockReturnValue({ id: null });
      jest.spyOn(departmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ department: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: department }));
      saveSubject.complete();

      // THEN
      expect(departmentFormService.getDepartment).toHaveBeenCalled();
      expect(departmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDepartment>>();
      const department = { id: 29518 };
      jest.spyOn(departmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ department });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(departmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDepartment', () => {
      it('should forward to departmentService', () => {
        const entity = { id: 29518 };
        const entity2 = { id: 15970 };
        jest.spyOn(departmentService, 'compareDepartment');
        comp.compareDepartment(entity, entity2);
        expect(departmentService.compareDepartment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
