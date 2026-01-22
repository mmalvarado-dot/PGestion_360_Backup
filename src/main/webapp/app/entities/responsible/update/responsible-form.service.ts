import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IResponsible, NewResponsible } from '../responsible.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResponsible for edit and NewResponsibleFormGroupInput for create.
 */
type ResponsibleFormGroupInput = IResponsible | PartialWithRequiredKeyOf<NewResponsible>;

type ResponsibleFormDefaults = Pick<NewResponsible, 'id'>;

type ResponsibleFormGroupContent = {
  id: FormControl<IResponsible['id'] | NewResponsible['id']>;
  name: FormControl<IResponsible['name']>;
  position: FormControl<IResponsible['position']>;
};

export type ResponsibleFormGroup = FormGroup<ResponsibleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResponsibleFormService {
  createResponsibleFormGroup(responsible: ResponsibleFormGroupInput = { id: null }): ResponsibleFormGroup {
    const responsibleRawValue = {
      ...this.getFormDefaults(),
      ...responsible,
    };
    return new FormGroup<ResponsibleFormGroupContent>({
      id: new FormControl(
        { value: responsibleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(responsibleRawValue.name, {
        validators: [Validators.required],
      }),
      position: new FormControl(responsibleRawValue.position, {
        validators: [Validators.required],
      }),
    });
  }

  getResponsible(form: ResponsibleFormGroup): IResponsible | NewResponsible {
    return form.getRawValue() as IResponsible | NewResponsible;
  }

  resetForm(form: ResponsibleFormGroup, responsible: ResponsibleFormGroupInput): void {
    const responsibleRawValue = { ...this.getFormDefaults(), ...responsible };
    form.reset(
      {
        ...responsibleRawValue,
        id: { value: responsibleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ResponsibleFormDefaults {
    return {
      id: null,
    };
  }
}
