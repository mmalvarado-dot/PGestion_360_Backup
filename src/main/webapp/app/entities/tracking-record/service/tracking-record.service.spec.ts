import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ITrackingRecord } from '../tracking-record.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tracking-record.test-samples';

import { RestTrackingRecord, TrackingRecordService } from './tracking-record.service';

const requireRestSample: RestTrackingRecord = {
  ...sampleWithRequiredData,
  changeDate: sampleWithRequiredData.changeDate?.format(DATE_FORMAT),
};

describe('TrackingRecord Service', () => {
  let service: TrackingRecordService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackingRecord | ITrackingRecord[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TrackingRecordService);
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

    it('should create a TrackingRecord', () => {
      const trackingRecord = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackingRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackingRecord', () => {
      const trackingRecord = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackingRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackingRecord', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackingRecord', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackingRecord', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackingRecordToCollectionIfMissing', () => {
      it('should add a TrackingRecord to an empty array', () => {
        const trackingRecord: ITrackingRecord = sampleWithRequiredData;
        expectedResult = service.addTrackingRecordToCollectionIfMissing([], trackingRecord);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackingRecord);
      });

      it('should not add a TrackingRecord to an array that contains it', () => {
        const trackingRecord: ITrackingRecord = sampleWithRequiredData;
        const trackingRecordCollection: ITrackingRecord[] = [
          {
            ...trackingRecord,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackingRecordToCollectionIfMissing(trackingRecordCollection, trackingRecord);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackingRecord to an array that doesn't contain it", () => {
        const trackingRecord: ITrackingRecord = sampleWithRequiredData;
        const trackingRecordCollection: ITrackingRecord[] = [sampleWithPartialData];
        expectedResult = service.addTrackingRecordToCollectionIfMissing(trackingRecordCollection, trackingRecord);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackingRecord);
      });

      it('should add only unique TrackingRecord to an array', () => {
        const trackingRecordArray: ITrackingRecord[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const trackingRecordCollection: ITrackingRecord[] = [sampleWithRequiredData];
        expectedResult = service.addTrackingRecordToCollectionIfMissing(trackingRecordCollection, ...trackingRecordArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackingRecord: ITrackingRecord = sampleWithRequiredData;
        const trackingRecord2: ITrackingRecord = sampleWithPartialData;
        expectedResult = service.addTrackingRecordToCollectionIfMissing([], trackingRecord, trackingRecord2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackingRecord);
        expect(expectedResult).toContain(trackingRecord2);
      });

      it('should accept null and undefined values', () => {
        const trackingRecord: ITrackingRecord = sampleWithRequiredData;
        expectedResult = service.addTrackingRecordToCollectionIfMissing([], null, trackingRecord, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackingRecord);
      });

      it('should return initial array if no TrackingRecord is added', () => {
        const trackingRecordCollection: ITrackingRecord[] = [sampleWithRequiredData];
        expectedResult = service.addTrackingRecordToCollectionIfMissing(trackingRecordCollection, undefined, null);
        expect(expectedResult).toEqual(trackingRecordCollection);
      });
    });

    describe('compareTrackingRecord', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackingRecord(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14377 };
        const entity2 = null;

        const compareResult1 = service.compareTrackingRecord(entity1, entity2);
        const compareResult2 = service.compareTrackingRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14377 };
        const entity2 = { id: 16399 };

        const compareResult1 = service.compareTrackingRecord(entity1, entity2);
        const compareResult2 = service.compareTrackingRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14377 };
        const entity2 = { id: 14377 };

        const compareResult1 = service.compareTrackingRecord(entity1, entity2);
        const compareResult2 = service.compareTrackingRecord(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
