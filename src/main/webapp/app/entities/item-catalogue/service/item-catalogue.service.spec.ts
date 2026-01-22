import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IItemCatalogue } from '../item-catalogue.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../item-catalogue.test-samples';

import { ItemCatalogueService } from './item-catalogue.service';

const requireRestSample: IItemCatalogue = {
  ...sampleWithRequiredData,
};

describe('ItemCatalogue Service', () => {
  let service: ItemCatalogueService;
  let httpMock: HttpTestingController;
  let expectedResult: IItemCatalogue | IItemCatalogue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ItemCatalogueService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ItemCatalogue', () => {
      const itemCatalogue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(itemCatalogue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ItemCatalogue', () => {
      const itemCatalogue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(itemCatalogue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ItemCatalogue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ItemCatalogue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ItemCatalogue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addItemCatalogueToCollectionIfMissing', () => {
      it('should add a ItemCatalogue to an empty array', () => {
        const itemCatalogue: IItemCatalogue = sampleWithRequiredData;
        expectedResult = service.addItemCatalogueToCollectionIfMissing([], itemCatalogue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(itemCatalogue);
      });

      it('should not add a ItemCatalogue to an array that contains it', () => {
        const itemCatalogue: IItemCatalogue = sampleWithRequiredData;
        const itemCatalogueCollection: IItemCatalogue[] = [
          {
            ...itemCatalogue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addItemCatalogueToCollectionIfMissing(itemCatalogueCollection, itemCatalogue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ItemCatalogue to an array that doesn't contain it", () => {
        const itemCatalogue: IItemCatalogue = sampleWithRequiredData;
        const itemCatalogueCollection: IItemCatalogue[] = [sampleWithPartialData];
        expectedResult = service.addItemCatalogueToCollectionIfMissing(itemCatalogueCollection, itemCatalogue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(itemCatalogue);
      });

      it('should add only unique ItemCatalogue to an array', () => {
        const itemCatalogueArray: IItemCatalogue[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const itemCatalogueCollection: IItemCatalogue[] = [sampleWithRequiredData];
        expectedResult = service.addItemCatalogueToCollectionIfMissing(itemCatalogueCollection, ...itemCatalogueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const itemCatalogue: IItemCatalogue = sampleWithRequiredData;
        const itemCatalogue2: IItemCatalogue = sampleWithPartialData;
        expectedResult = service.addItemCatalogueToCollectionIfMissing([], itemCatalogue, itemCatalogue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(itemCatalogue);
        expect(expectedResult).toContain(itemCatalogue2);
      });

      it('should accept null and undefined values', () => {
        const itemCatalogue: IItemCatalogue = sampleWithRequiredData;
        expectedResult = service.addItemCatalogueToCollectionIfMissing([], null, itemCatalogue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(itemCatalogue);
      });

      it('should return initial array if no ItemCatalogue is added', () => {
        const itemCatalogueCollection: IItemCatalogue[] = [sampleWithRequiredData];
        expectedResult = service.addItemCatalogueToCollectionIfMissing(itemCatalogueCollection, undefined, null);
        expect(expectedResult).toEqual(itemCatalogueCollection);
      });
    });

    describe('compareItemCatalogue', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareItemCatalogue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24609 };
        const entity2 = null;

        const compareResult1 = service.compareItemCatalogue(entity1, entity2);
        const compareResult2 = service.compareItemCatalogue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24609 };
        const entity2 = { id: 3864 };

        const compareResult1 = service.compareItemCatalogue(entity1, entity2);
        const compareResult2 = service.compareItemCatalogue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24609 };
        const entity2 = { id: 24609 };

        const compareResult1 = service.compareItemCatalogue(entity1, entity2);
        const compareResult2 = service.compareItemCatalogue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
