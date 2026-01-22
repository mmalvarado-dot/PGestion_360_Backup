import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tracking-record.test-samples';

import { TrackingRecordFormService } from './tracking-record-form.service';

describe('TrackingRecord Form Service', () => {
  let service: TrackingRecordFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackingRecordFormService);
  });

  describe('Service methods', () => {
    describe('createTrackingRecordFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackingRecordFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            changeDate: expect.any(Object),
            status: expect.any(Object),
            comments: expect.any(Object),
            user: expect.any(Object),
            responsible: expect.any(Object),
            changeRequest: expect.any(Object),
          }),
        );
      });

      it('passing ITrackingRecord should create a new form with FormGroup', () => {
        const formGroup = service.createTrackingRecordFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            changeDate: expect.any(Object),
            status: expect.any(Object),
            comments: expect.any(Object),
            user: expect.any(Object),
            responsible: expect.any(Object),
            changeRequest: expect.any(Object),
          }),
        );
      });
    });

    describe('getTrackingRecord', () => {
      it('should return NewTrackingRecord for default TrackingRecord initial value', () => {
        const formGroup = service.createTrackingRecordFormGroup(sampleWithNewData);

        const trackingRecord = service.getTrackingRecord(formGroup) as any;

        expect(trackingRecord).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackingRecord for empty TrackingRecord initial value', () => {
        const formGroup = service.createTrackingRecordFormGroup();

        const trackingRecord = service.getTrackingRecord(formGroup) as any;

        expect(trackingRecord).toMatchObject({});
      });

      it('should return ITrackingRecord', () => {
        const formGroup = service.createTrackingRecordFormGroup(sampleWithRequiredData);

        const trackingRecord = service.getTrackingRecord(formGroup) as any;

        expect(trackingRecord).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackingRecord should not enable id FormControl', () => {
        const formGroup = service.createTrackingRecordFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackingRecord should disable id FormControl', () => {
        const formGroup = service.createTrackingRecordFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
