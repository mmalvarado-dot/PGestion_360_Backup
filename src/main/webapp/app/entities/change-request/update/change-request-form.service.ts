import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

// 1. IMPORTACIÓN AGREGADA: Necesaria para generar la fecha de hoy
import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IChangeRequest, NewChangeRequest } from '../change-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IChangeRequest for edit and NewChangeRequestFormGroupInput for create.
 */
type ChangeRequestFormGroupInput = IChangeRequest | PartialWithRequiredKeyOf<NewChangeRequest>;

// 2. TIPO ACTUALIZADO: Agregamos 'createdDate' y 'status' para poder darles valores por defecto
type ChangeRequestFormDefaults = Pick<NewChangeRequest, 'id' | 'createdDate' | 'status'>;

type ChangeRequestFormGroupContent = {
  id: FormControl<IChangeRequest['id'] | NewChangeRequest['id']>;
  title: FormControl<IChangeRequest['title']>;
  description: FormControl<IChangeRequest['description']>;
  createdDate: FormControl<IChangeRequest['createdDate']>;
  updatedDate: FormControl<IChangeRequest['updatedDate']>;
  priority: FormControl<IChangeRequest['priority']>;
  impact: FormControl<IChangeRequest['impact']>;
  status: FormControl<IChangeRequest['status']>;
  fechaEntrega: FormControl<IChangeRequest['fechaEntrega']>;
  observaciones: FormControl<IChangeRequest['observaciones']>;
  archivoAdjunto: FormControl<IChangeRequest['archivoAdjunto']>;
  archivoAdjuntoContentType: FormControl<IChangeRequest['archivoAdjuntoContentType']>;
  solicitante: FormControl<IChangeRequest['solicitante']>;
  departamento: FormControl<IChangeRequest['departamento']>;
  responsible: FormControl<IChangeRequest['responsible']>;
  itemCatalogue: FormControl<IChangeRequest['itemCatalogue']>;
};

export type ChangeRequestFormGroup = FormGroup<ChangeRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ChangeRequestFormService {
  createChangeRequestFormGroup(changeRequest: ChangeRequestFormGroupInput = { id: null }): ChangeRequestFormGroup {
    const changeRequestRawValue = {
      ...this.getFormDefaults(),
      ...changeRequest,
    };
    return new FormGroup<ChangeRequestFormGroupContent>({
      id: new FormControl(
        { value: changeRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(changeRequestRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(changeRequestRawValue.description, {
        validators: [Validators.required],
      }),
      createdDate: new FormControl(changeRequestRawValue.createdDate, {
        validators: [Validators.required],
      }),
      updatedDate: new FormControl(changeRequestRawValue.updatedDate),
      priority: new FormControl(changeRequestRawValue.priority),
      impact: new FormControl(changeRequestRawValue.impact),
      status: new FormControl(changeRequestRawValue.status, {
        validators: [Validators.required],
      }),
      fechaEntrega: new FormControl(changeRequestRawValue.fechaEntrega),
      observaciones: new FormControl(changeRequestRawValue.observaciones),
      archivoAdjunto: new FormControl(changeRequestRawValue.archivoAdjunto),
      archivoAdjuntoContentType: new FormControl(changeRequestRawValue.archivoAdjuntoContentType),
      solicitante: new FormControl(changeRequestRawValue.solicitante),
      departamento: new FormControl(changeRequestRawValue.departamento),
      responsible: new FormControl(changeRequestRawValue.responsible),
      itemCatalogue: new FormControl(changeRequestRawValue.itemCatalogue),
    });
  }

  getChangeRequest(form: ChangeRequestFormGroup): IChangeRequest | NewChangeRequest {
    return form.getRawValue() as IChangeRequest | NewChangeRequest;
  }

  resetForm(form: ChangeRequestFormGroup, changeRequest: ChangeRequestFormGroupInput): void {
    const changeRequestRawValue = { ...this.getFormDefaults(), ...changeRequest };
    form.reset(
      {
        ...changeRequestRawValue,
        id: { value: changeRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  // 3. LÓGICA AGREGADA: Aquí definimos los valores iniciales
  private getFormDefaults(): ChangeRequestFormDefaults {
    return {
      id: null,
      createdDate: dayjs(), // Pone la fecha y hora actual
      status: 'PENDIENTE', // Pone el estado inicial (Revisa mayúsculas/minúsculas si falla)
    } as any;
  }
}
