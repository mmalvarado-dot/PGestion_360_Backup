import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IFileRecord } from '../file-record.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../file-record.test-samples';

import { FileRecordService } from './file-record.service';

const requireRestSample: IFileRecord = {
  ...sampleWithRequiredData,
};

describe('FileRecord Service', () => {
  let service: FileRecordService;
  let httpMock: HttpTestingController;
  let expectedResult: IFileRecord | IFileRecord[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FileRecordService);
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

    it('should create a FileRecord', () => {
      const fileRecord = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(fileRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FileRecord', () => {
      const fileRecord = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(fileRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FileRecord', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FileRecord', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FileRecord', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFileRecordToCollectionIfMissing', () => {
      it('should add a FileRecord to an empty array', () => {
        const fileRecord: IFileRecord = sampleWithRequiredData;
        expectedResult = service.addFileRecordToCollectionIfMissing([], fileRecord);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fileRecord);
      });

      it('should not add a FileRecord to an array that contains it', () => {
        const fileRecord: IFileRecord = sampleWithRequiredData;
        const fileRecordCollection: IFileRecord[] = [
          {
            ...fileRecord,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFileRecordToCollectionIfMissing(fileRecordCollection, fileRecord);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FileRecord to an array that doesn't contain it", () => {
        const fileRecord: IFileRecord = sampleWithRequiredData;
        const fileRecordCollection: IFileRecord[] = [sampleWithPartialData];
        expectedResult = service.addFileRecordToCollectionIfMissing(fileRecordCollection, fileRecord);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fileRecord);
      });

      it('should add only unique FileRecord to an array', () => {
        const fileRecordArray: IFileRecord[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const fileRecordCollection: IFileRecord[] = [sampleWithRequiredData];
        expectedResult = service.addFileRecordToCollectionIfMissing(fileRecordCollection, ...fileRecordArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const fileRecord: IFileRecord = sampleWithRequiredData;
        const fileRecord2: IFileRecord = sampleWithPartialData;
        expectedResult = service.addFileRecordToCollectionIfMissing([], fileRecord, fileRecord2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fileRecord);
        expect(expectedResult).toContain(fileRecord2);
      });

      it('should accept null and undefined values', () => {
        const fileRecord: IFileRecord = sampleWithRequiredData;
        expectedResult = service.addFileRecordToCollectionIfMissing([], null, fileRecord, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fileRecord);
      });

      it('should return initial array if no FileRecord is added', () => {
        const fileRecordCollection: IFileRecord[] = [sampleWithRequiredData];
        expectedResult = service.addFileRecordToCollectionIfMissing(fileRecordCollection, undefined, null);
        expect(expectedResult).toEqual(fileRecordCollection);
      });
    });

    describe('compareFileRecord', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFileRecord(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10709 };
        const entity2 = null;

        const compareResult1 = service.compareFileRecord(entity1, entity2);
        const compareResult2 = service.compareFileRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10709 };
        const entity2 = { id: 25181 };

        const compareResult1 = service.compareFileRecord(entity1, entity2);
        const compareResult2 = service.compareFileRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 10709 };
        const entity2 = { id: 10709 };

        const compareResult1 = service.compareFileRecord(entity1, entity2);
        const compareResult2 = service.compareFileRecord(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
