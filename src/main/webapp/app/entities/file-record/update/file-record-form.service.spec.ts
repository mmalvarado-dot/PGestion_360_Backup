import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../file-record.test-samples';

import { FileRecordFormService } from './file-record-form.service';

describe('FileRecord Form Service', () => {
  let service: FileRecordFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileRecordFormService);
  });

  describe('Service methods', () => {
    describe('createFileRecordFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFileRecordFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fileName: expect.any(Object),
            filePath: expect.any(Object),
            fileType: expect.any(Object),
            content: expect.any(Object),
            changeRequest: expect.any(Object),
          }),
        );
      });

      it('passing IFileRecord should create a new form with FormGroup', () => {
        const formGroup = service.createFileRecordFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fileName: expect.any(Object),
            filePath: expect.any(Object),
            fileType: expect.any(Object),
            content: expect.any(Object),
            changeRequest: expect.any(Object),
          }),
        );
      });
    });

    describe('getFileRecord', () => {
      it('should return NewFileRecord for default FileRecord initial value', () => {
        const formGroup = service.createFileRecordFormGroup(sampleWithNewData);

        const fileRecord = service.getFileRecord(formGroup) as any;

        expect(fileRecord).toMatchObject(sampleWithNewData);
      });

      it('should return NewFileRecord for empty FileRecord initial value', () => {
        const formGroup = service.createFileRecordFormGroup();

        const fileRecord = service.getFileRecord(formGroup) as any;

        expect(fileRecord).toMatchObject({});
      });

      it('should return IFileRecord', () => {
        const formGroup = service.createFileRecordFormGroup(sampleWithRequiredData);

        const fileRecord = service.getFileRecord(formGroup) as any;

        expect(fileRecord).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFileRecord should not enable id FormControl', () => {
        const formGroup = service.createFileRecordFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFileRecord should disable id FormControl', () => {
        const formGroup = service.createFileRecordFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
