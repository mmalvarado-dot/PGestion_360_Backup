import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITrackingRecord, NewTrackingRecord } from '../tracking-record.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackingRecord for edit and NewTrackingRecordFormGroupInput for create.
 */
type TrackingRecordFormGroupInput = ITrackingRecord | PartialWithRequiredKeyOf<NewTrackingRecord>;

type TrackingRecordFormDefaults = Pick<NewTrackingRecord, 'id'>;

type TrackingRecordFormGroupContent = {
  id: FormControl<ITrackingRecord['id'] | NewTrackingRecord['id']>;
  changeDate: FormControl<ITrackingRecord['changeDate']>;
  status: FormControl<ITrackingRecord['status']>;
  comments: FormControl<ITrackingRecord['comments']>;
  user: FormControl<ITrackingRecord['user']>;
  changeRequest: FormControl<ITrackingRecord['changeRequest']>;
  department: FormControl<ITrackingRecord['department']>;
};

export type TrackingRecordFormGroup = FormGroup<TrackingRecordFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackingRecordFormService {
  createTrackingRecordFormGroup(trackingRecord: TrackingRecordFormGroupInput = { id: null }): TrackingRecordFormGroup {
    const trackingRecordRawValue = {
      ...this.getFormDefaults(),
      ...trackingRecord,
    };
    return new FormGroup<TrackingRecordFormGroupContent>({
      id: new FormControl(
        { value: trackingRecordRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      changeDate: new FormControl(trackingRecordRawValue.changeDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(trackingRecordRawValue.status, {
        validators: [Validators.required],
      }),
      comments: new FormControl(trackingRecordRawValue.comments),
      user: new FormControl(trackingRecordRawValue.user),
      changeRequest: new FormControl(trackingRecordRawValue.changeRequest),
      department: new FormControl(trackingRecordRawValue.department),
    });
  }

  getTrackingRecord(form: TrackingRecordFormGroup): ITrackingRecord | NewTrackingRecord {
    return form.getRawValue() as ITrackingRecord | NewTrackingRecord;
  }

  resetForm(form: TrackingRecordFormGroup, trackingRecord: TrackingRecordFormGroupInput): void {
    const trackingRecordRawValue = { ...this.getFormDefaults(), ...trackingRecord };
    form.reset(
      {
        ...trackingRecordRawValue,
        id: { value: trackingRecordRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TrackingRecordFormDefaults {
    return {
      id: null,
    };
  }
}
