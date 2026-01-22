import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../responsible.test-samples';

import { ResponsibleFormService } from './responsible-form.service';

describe('Responsible Form Service', () => {
  let service: ResponsibleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResponsibleFormService);
  });

  describe('Service methods', () => {
    describe('createResponsibleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createResponsibleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            position: expect.any(Object),
          }),
        );
      });

      it('passing IResponsible should create a new form with FormGroup', () => {
        const formGroup = service.createResponsibleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            position: expect.any(Object),
          }),
        );
      });
    });

    describe('getResponsible', () => {
      it('should return NewResponsible for default Responsible initial value', () => {
        const formGroup = service.createResponsibleFormGroup(sampleWithNewData);

        const responsible = service.getResponsible(formGroup) as any;

        expect(responsible).toMatchObject(sampleWithNewData);
      });

      it('should return NewResponsible for empty Responsible initial value', () => {
        const formGroup = service.createResponsibleFormGroup();

        const responsible = service.getResponsible(formGroup) as any;

        expect(responsible).toMatchObject({});
      });

      it('should return IResponsible', () => {
        const formGroup = service.createResponsibleFormGroup(sampleWithRequiredData);

        const responsible = service.getResponsible(formGroup) as any;

        expect(responsible).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IResponsible should not enable id FormControl', () => {
        const formGroup = service.createResponsibleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewResponsible should disable id FormControl', () => {
        const formGroup = service.createResponsibleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
