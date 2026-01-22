import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICatalogue, NewCatalogue } from '../catalogue.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICatalogue for edit and NewCatalogueFormGroupInput for create.
 */
type CatalogueFormGroupInput = ICatalogue | PartialWithRequiredKeyOf<NewCatalogue>;

type CatalogueFormDefaults = Pick<NewCatalogue, 'id' | 'status'>;

type CatalogueFormGroupContent = {
  id: FormControl<ICatalogue['id'] | NewCatalogue['id']>;
  name: FormControl<ICatalogue['name']>;
  code: FormControl<ICatalogue['code']>;
  status: FormControl<ICatalogue['status']>;
};

export type CatalogueFormGroup = FormGroup<CatalogueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CatalogueFormService {
  createCatalogueFormGroup(catalogue: CatalogueFormGroupInput = { id: null }): CatalogueFormGroup {
    const catalogueRawValue = {
      ...this.getFormDefaults(),
      ...catalogue,
    };
    return new FormGroup<CatalogueFormGroupContent>({
      id: new FormControl(
        { value: catalogueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(catalogueRawValue.name, {
        validators: [Validators.required],
      }),
      code: new FormControl(catalogueRawValue.code, {
        validators: [Validators.required],
      }),
      status: new FormControl(catalogueRawValue.status),
    });
  }

  getCatalogue(form: CatalogueFormGroup): ICatalogue | NewCatalogue {
    return form.getRawValue() as ICatalogue | NewCatalogue;
  }

  resetForm(form: CatalogueFormGroup, catalogue: CatalogueFormGroupInput): void {
    const catalogueRawValue = { ...this.getFormDefaults(), ...catalogue };
    form.reset(
      {
        ...catalogueRawValue,
        id: { value: catalogueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CatalogueFormDefaults {
    return {
      id: null,
      status: false,
    };
  }
}
