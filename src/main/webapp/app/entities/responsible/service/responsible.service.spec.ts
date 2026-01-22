import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IResponsible } from '../responsible.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../responsible.test-samples';

import { ResponsibleService } from './responsible.service';

const requireRestSample: IResponsible = {
  ...sampleWithRequiredData,
};

describe('Responsible Service', () => {
  let service: ResponsibleService;
  let httpMock: HttpTestingController;
  let expectedResult: IResponsible | IResponsible[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ResponsibleService);
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

    it('should create a Responsible', () => {
      const responsible = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(responsible).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Responsible', () => {
      const responsible = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(responsible).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Responsible', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Responsible', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Responsible', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addResponsibleToCollectionIfMissing', () => {
      it('should add a Responsible to an empty array', () => {
        const responsible: IResponsible = sampleWithRequiredData;
        expectedResult = service.addResponsibleToCollectionIfMissing([], responsible);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(responsible);
      });

      it('should not add a Responsible to an array that contains it', () => {
        const responsible: IResponsible = sampleWithRequiredData;
        const responsibleCollection: IResponsible[] = [
          {
            ...responsible,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addResponsibleToCollectionIfMissing(responsibleCollection, responsible);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Responsible to an array that doesn't contain it", () => {
        const responsible: IResponsible = sampleWithRequiredData;
        const responsibleCollection: IResponsible[] = [sampleWithPartialData];
        expectedResult = service.addResponsibleToCollectionIfMissing(responsibleCollection, responsible);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(responsible);
      });

      it('should add only unique Responsible to an array', () => {
        const responsibleArray: IResponsible[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const responsibleCollection: IResponsible[] = [sampleWithRequiredData];
        expectedResult = service.addResponsibleToCollectionIfMissing(responsibleCollection, ...responsibleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const responsible: IResponsible = sampleWithRequiredData;
        const responsible2: IResponsible = sampleWithPartialData;
        expectedResult = service.addResponsibleToCollectionIfMissing([], responsible, responsible2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(responsible);
        expect(expectedResult).toContain(responsible2);
      });

      it('should accept null and undefined values', () => {
        const responsible: IResponsible = sampleWithRequiredData;
        expectedResult = service.addResponsibleToCollectionIfMissing([], null, responsible, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(responsible);
      });

      it('should return initial array if no Responsible is added', () => {
        const responsibleCollection: IResponsible[] = [sampleWithRequiredData];
        expectedResult = service.addResponsibleToCollectionIfMissing(responsibleCollection, undefined, null);
        expect(expectedResult).toEqual(responsibleCollection);
      });
    });

    describe('compareResponsible', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareResponsible(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24783 };
        const entity2 = null;

        const compareResult1 = service.compareResponsible(entity1, entity2);
        const compareResult2 = service.compareResponsible(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24783 };
        const entity2 = { id: 5616 };

        const compareResult1 = service.compareResponsible(entity1, entity2);
        const compareResult2 = service.compareResponsible(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24783 };
        const entity2 = { id: 24783 };

        const compareResult1 = service.compareResponsible(entity1, entity2);
        const compareResult2 = service.compareResponsible(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
