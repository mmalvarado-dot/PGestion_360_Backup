import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IChangeRequest } from '../change-request.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../change-request.test-samples';

import { ChangeRequestService, RestChangeRequest } from './change-request.service';

const requireRestSample: RestChangeRequest = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.format(DATE_FORMAT),
  updatedDate: sampleWithRequiredData.updatedDate?.format(DATE_FORMAT),
  fechaEntrega: sampleWithRequiredData.fechaEntrega?.format(DATE_FORMAT),
};

describe('ChangeRequest Service', () => {
  let service: ChangeRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: IChangeRequest | IChangeRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ChangeRequestService);
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

    it('should create a ChangeRequest', () => {
      const changeRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(changeRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ChangeRequest', () => {
      const changeRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(changeRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ChangeRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ChangeRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ChangeRequest', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addChangeRequestToCollectionIfMissing', () => {
      it('should add a ChangeRequest to an empty array', () => {
        const changeRequest: IChangeRequest = sampleWithRequiredData;
        expectedResult = service.addChangeRequestToCollectionIfMissing([], changeRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(changeRequest);
      });

      it('should not add a ChangeRequest to an array that contains it', () => {
        const changeRequest: IChangeRequest = sampleWithRequiredData;
        const changeRequestCollection: IChangeRequest[] = [
          {
            ...changeRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addChangeRequestToCollectionIfMissing(changeRequestCollection, changeRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ChangeRequest to an array that doesn't contain it", () => {
        const changeRequest: IChangeRequest = sampleWithRequiredData;
        const changeRequestCollection: IChangeRequest[] = [sampleWithPartialData];
        expectedResult = service.addChangeRequestToCollectionIfMissing(changeRequestCollection, changeRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(changeRequest);
      });

      it('should add only unique ChangeRequest to an array', () => {
        const changeRequestArray: IChangeRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const changeRequestCollection: IChangeRequest[] = [sampleWithRequiredData];
        expectedResult = service.addChangeRequestToCollectionIfMissing(changeRequestCollection, ...changeRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const changeRequest: IChangeRequest = sampleWithRequiredData;
        const changeRequest2: IChangeRequest = sampleWithPartialData;
        expectedResult = service.addChangeRequestToCollectionIfMissing([], changeRequest, changeRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(changeRequest);
        expect(expectedResult).toContain(changeRequest2);
      });

      it('should accept null and undefined values', () => {
        const changeRequest: IChangeRequest = sampleWithRequiredData;
        expectedResult = service.addChangeRequestToCollectionIfMissing([], null, changeRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(changeRequest);
      });

      it('should return initial array if no ChangeRequest is added', () => {
        const changeRequestCollection: IChangeRequest[] = [sampleWithRequiredData];
        expectedResult = service.addChangeRequestToCollectionIfMissing(changeRequestCollection, undefined, null);
        expect(expectedResult).toEqual(changeRequestCollection);
      });
    });

    describe('compareChangeRequest', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareChangeRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 26371 };
        const entity2 = null;

        const compareResult1 = service.compareChangeRequest(entity1, entity2);
        const compareResult2 = service.compareChangeRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 26371 };
        const entity2 = { id: 20589 };

        const compareResult1 = service.compareChangeRequest(entity1, entity2);
        const compareResult2 = service.compareChangeRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 26371 };
        const entity2 = { id: 26371 };

        const compareResult1 = service.compareChangeRequest(entity1, entity2);
        const compareResult2 = service.compareChangeRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
