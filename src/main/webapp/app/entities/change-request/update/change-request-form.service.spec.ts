import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../change-request.test-samples';

import { ChangeRequestFormService } from './change-request-form.service';

describe('ChangeRequest Form Service', () => {
  let service: ChangeRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChangeRequestFormService);
  });

  describe('Service methods', () => {
    describe('createChangeRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createChangeRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            createdDate: expect.any(Object),
            updatedDate: expect.any(Object),
            priority: expect.any(Object),
            impact: expect.any(Object),
            status: expect.any(Object),
            fechaEntrega: expect.any(Object),
            observaciones: expect.any(Object),
            archivoAdjunto: expect.any(Object),
            solicitante: expect.any(Object),
            departamento: expect.any(Object),
            responsible: expect.any(Object),
            itemCatalogue: expect.any(Object),
          }),
        );
      });

      it('passing IChangeRequest should create a new form with FormGroup', () => {
        const formGroup = service.createChangeRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            createdDate: expect.any(Object),
            updatedDate: expect.any(Object),
            priority: expect.any(Object),
            impact: expect.any(Object),
            status: expect.any(Object),
            fechaEntrega: expect.any(Object),
            observaciones: expect.any(Object),
            archivoAdjunto: expect.any(Object),
            solicitante: expect.any(Object),
            departamento: expect.any(Object),
            responsible: expect.any(Object),
            itemCatalogue: expect.any(Object),
          }),
        );
      });
    });

    describe('getChangeRequest', () => {
      it('should return NewChangeRequest for default ChangeRequest initial value', () => {
        const formGroup = service.createChangeRequestFormGroup(sampleWithNewData);

        const changeRequest = service.getChangeRequest(formGroup) as any;

        expect(changeRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewChangeRequest for empty ChangeRequest initial value', () => {
        const formGroup = service.createChangeRequestFormGroup();

        const changeRequest = service.getChangeRequest(formGroup) as any;

        expect(changeRequest).toMatchObject({});
      });

      it('should return IChangeRequest', () => {
        const formGroup = service.createChangeRequestFormGroup(sampleWithRequiredData);

        const changeRequest = service.getChangeRequest(formGroup) as any;

        expect(changeRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IChangeRequest should not enable id FormControl', () => {
        const formGroup = service.createChangeRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewChangeRequest should disable id FormControl', () => {
        const formGroup = service.createChangeRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
