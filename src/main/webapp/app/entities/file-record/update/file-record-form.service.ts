import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFileRecord, NewFileRecord } from '../file-record.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFileRecord for edit and NewFileRecordFormGroupInput for create.
 */
type FileRecordFormGroupInput = IFileRecord | PartialWithRequiredKeyOf<NewFileRecord>;

type FileRecordFormDefaults = Pick<NewFileRecord, 'id'>;

type FileRecordFormGroupContent = {
  id: FormControl<IFileRecord['id'] | NewFileRecord['id']>;
  fileName: FormControl<IFileRecord['fileName']>;
  filePath: FormControl<IFileRecord['filePath']>;
  fileType: FormControl<IFileRecord['fileType']>;
  content: FormControl<IFileRecord['content']>;
  contentContentType: FormControl<IFileRecord['contentContentType']>;
  changeRequest: FormControl<IFileRecord['changeRequest']>;
};

export type FileRecordFormGroup = FormGroup<FileRecordFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FileRecordFormService {
  createFileRecordFormGroup(fileRecord: FileRecordFormGroupInput = { id: null }): FileRecordFormGroup {
    const fileRecordRawValue = {
      ...this.getFormDefaults(),
      ...fileRecord,
    };
    return new FormGroup<FileRecordFormGroupContent>({
      id: new FormControl(
        { value: fileRecordRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fileName: new FormControl(fileRecordRawValue.fileName, {
        validators: [Validators.required],
      }),
      filePath: new FormControl(fileRecordRawValue.filePath, {
        validators: [Validators.required],
      }),
      fileType: new FormControl(fileRecordRawValue.fileType, {
        validators: [Validators.required],
      }),
      content: new FormControl(fileRecordRawValue.content),
      contentContentType: new FormControl(fileRecordRawValue.contentContentType),
      changeRequest: new FormControl(fileRecordRawValue.changeRequest),
    });
  }

  getFileRecord(form: FileRecordFormGroup): IFileRecord | NewFileRecord {
    return form.getRawValue() as IFileRecord | NewFileRecord;
  }

  resetForm(form: FileRecordFormGroup, fileRecord: FileRecordFormGroupInput): void {
    const fileRecordRawValue = { ...this.getFormDefaults(), ...fileRecord };
    form.reset(
      {
        ...fileRecordRawValue,
        id: { value: fileRecordRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FileRecordFormDefaults {
    return {
      id: null,
    };
  }
}
