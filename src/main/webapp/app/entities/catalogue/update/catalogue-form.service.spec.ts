import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../catalogue.test-samples';

import { CatalogueFormService } from './catalogue-form.service';

describe('Catalogue Form Service', () => {
  let service: CatalogueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CatalogueFormService);
  });

  describe('Service methods', () => {
    describe('createCatalogueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCatalogueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });

      it('passing ICatalogue should create a new form with FormGroup', () => {
        const formGroup = service.createCatalogueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });
    });

    describe('getCatalogue', () => {
      it('should return NewCatalogue for default Catalogue initial value', () => {
        const formGroup = service.createCatalogueFormGroup(sampleWithNewData);

        const catalogue = service.getCatalogue(formGroup) as any;

        expect(catalogue).toMatchObject(sampleWithNewData);
      });

      it('should return NewCatalogue for empty Catalogue initial value', () => {
        const formGroup = service.createCatalogueFormGroup();

        const catalogue = service.getCatalogue(formGroup) as any;

        expect(catalogue).toMatchObject({});
      });

      it('should return ICatalogue', () => {
        const formGroup = service.createCatalogueFormGroup(sampleWithRequiredData);

        const catalogue = service.getCatalogue(formGroup) as any;

        expect(catalogue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICatalogue should not enable id FormControl', () => {
        const formGroup = service.createCatalogueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCatalogue should disable id FormControl', () => {
        const formGroup = service.createCatalogueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
