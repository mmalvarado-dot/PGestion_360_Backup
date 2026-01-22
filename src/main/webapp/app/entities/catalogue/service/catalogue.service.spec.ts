import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICatalogue } from '../catalogue.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../catalogue.test-samples';

import { CatalogueService } from './catalogue.service';

const requireRestSample: ICatalogue = {
  ...sampleWithRequiredData,
};

describe('Catalogue Service', () => {
  let service: CatalogueService;
  let httpMock: HttpTestingController;
  let expectedResult: ICatalogue | ICatalogue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CatalogueService);
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

    it('should create a Catalogue', () => {
      const catalogue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(catalogue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Catalogue', () => {
      const catalogue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(catalogue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Catalogue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Catalogue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Catalogue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCatalogueToCollectionIfMissing', () => {
      it('should add a Catalogue to an empty array', () => {
        const catalogue: ICatalogue = sampleWithRequiredData;
        expectedResult = service.addCatalogueToCollectionIfMissing([], catalogue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(catalogue);
      });

      it('should not add a Catalogue to an array that contains it', () => {
        const catalogue: ICatalogue = sampleWithRequiredData;
        const catalogueCollection: ICatalogue[] = [
          {
            ...catalogue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCatalogueToCollectionIfMissing(catalogueCollection, catalogue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Catalogue to an array that doesn't contain it", () => {
        const catalogue: ICatalogue = sampleWithRequiredData;
        const catalogueCollection: ICatalogue[] = [sampleWithPartialData];
        expectedResult = service.addCatalogueToCollectionIfMissing(catalogueCollection, catalogue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(catalogue);
      });

      it('should add only unique Catalogue to an array', () => {
        const catalogueArray: ICatalogue[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const catalogueCollection: ICatalogue[] = [sampleWithRequiredData];
        expectedResult = service.addCatalogueToCollectionIfMissing(catalogueCollection, ...catalogueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const catalogue: ICatalogue = sampleWithRequiredData;
        const catalogue2: ICatalogue = sampleWithPartialData;
        expectedResult = service.addCatalogueToCollectionIfMissing([], catalogue, catalogue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(catalogue);
        expect(expectedResult).toContain(catalogue2);
      });

      it('should accept null and undefined values', () => {
        const catalogue: ICatalogue = sampleWithRequiredData;
        expectedResult = service.addCatalogueToCollectionIfMissing([], null, catalogue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(catalogue);
      });

      it('should return initial array if no Catalogue is added', () => {
        const catalogueCollection: ICatalogue[] = [sampleWithRequiredData];
        expectedResult = service.addCatalogueToCollectionIfMissing(catalogueCollection, undefined, null);
        expect(expectedResult).toEqual(catalogueCollection);
      });
    });

    describe('compareCatalogue', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCatalogue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 7498 };
        const entity2 = null;

        const compareResult1 = service.compareCatalogue(entity1, entity2);
        const compareResult2 = service.compareCatalogue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 7498 };
        const entity2 = { id: 28253 };

        const compareResult1 = service.compareCatalogue(entity1, entity2);
        const compareResult2 = service.compareCatalogue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 7498 };
        const entity2 = { id: 7498 };

        const compareResult1 = service.compareCatalogue(entity1, entity2);
        const compareResult2 = service.compareCatalogue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
