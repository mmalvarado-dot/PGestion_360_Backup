import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../item-catalogue.test-samples';

import { ItemCatalogueFormService } from './item-catalogue-form.service';

describe('ItemCatalogue Form Service', () => {
  let service: ItemCatalogueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemCatalogueFormService);
  });

  describe('Service methods', () => {
    describe('createItemCatalogueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createItemCatalogueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            catalogueCode: expect.any(Object),
            active: expect.any(Object),
            catalogue: expect.any(Object),
          }),
        );
      });

      it('passing IItemCatalogue should create a new form with FormGroup', () => {
        const formGroup = service.createItemCatalogueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            catalogueCode: expect.any(Object),
            active: expect.any(Object),
            catalogue: expect.any(Object),
          }),
        );
      });
    });

    describe('getItemCatalogue', () => {
      it('should return NewItemCatalogue for default ItemCatalogue initial value', () => {
        const formGroup = service.createItemCatalogueFormGroup(sampleWithNewData);

        const itemCatalogue = service.getItemCatalogue(formGroup) as any;

        expect(itemCatalogue).toMatchObject(sampleWithNewData);
      });

      it('should return NewItemCatalogue for empty ItemCatalogue initial value', () => {
        const formGroup = service.createItemCatalogueFormGroup();

        const itemCatalogue = service.getItemCatalogue(formGroup) as any;

        expect(itemCatalogue).toMatchObject({});
      });

      it('should return IItemCatalogue', () => {
        const formGroup = service.createItemCatalogueFormGroup(sampleWithRequiredData);

        const itemCatalogue = service.getItemCatalogue(formGroup) as any;

        expect(itemCatalogue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IItemCatalogue should not enable id FormControl', () => {
        const formGroup = service.createItemCatalogueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewItemCatalogue should disable id FormControl', () => {
        const formGroup = service.createItemCatalogueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
