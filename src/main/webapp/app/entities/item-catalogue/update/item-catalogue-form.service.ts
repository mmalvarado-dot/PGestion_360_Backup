import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IItemCatalogue, NewItemCatalogue } from '../item-catalogue.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IItemCatalogue for edit and NewItemCatalogueFormGroupInput for create.
 */
type ItemCatalogueFormGroupInput = IItemCatalogue | PartialWithRequiredKeyOf<NewItemCatalogue>;

type ItemCatalogueFormDefaults = Pick<NewItemCatalogue, 'id' | 'active'>;

type ItemCatalogueFormGroupContent = {
  id: FormControl<IItemCatalogue['id'] | NewItemCatalogue['id']>;
  name: FormControl<IItemCatalogue['name']>;
  code: FormControl<IItemCatalogue['code']>;
  catalogueCode: FormControl<IItemCatalogue['catalogueCode']>;
  active: FormControl<IItemCatalogue['active']>;
  catalogue: FormControl<IItemCatalogue['catalogue']>;
};

export type ItemCatalogueFormGroup = FormGroup<ItemCatalogueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ItemCatalogueFormService {
  createItemCatalogueFormGroup(itemCatalogue: ItemCatalogueFormGroupInput = { id: null }): ItemCatalogueFormGroup {
    const itemCatalogueRawValue = {
      ...this.getFormDefaults(),
      ...itemCatalogue,
    };
    return new FormGroup<ItemCatalogueFormGroupContent>({
      id: new FormControl(
        { value: itemCatalogueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(itemCatalogueRawValue.name, {
        validators: [Validators.required],
      }),
      code: new FormControl(itemCatalogueRawValue.code, {
        validators: [Validators.required],
      }),
      catalogueCode: new FormControl(itemCatalogueRawValue.catalogueCode, {
        validators: [Validators.required],
      }),
      active: new FormControl(itemCatalogueRawValue.active),
      catalogue: new FormControl(itemCatalogueRawValue.catalogue),
    });
  }

  getItemCatalogue(form: ItemCatalogueFormGroup): IItemCatalogue | NewItemCatalogue {
    return form.getRawValue() as IItemCatalogue | NewItemCatalogue;
  }

  resetForm(form: ItemCatalogueFormGroup, itemCatalogue: ItemCatalogueFormGroupInput): void {
    const itemCatalogueRawValue = { ...this.getFormDefaults(), ...itemCatalogue };
    form.reset(
      {
        ...itemCatalogueRawValue,
        id: { value: itemCatalogueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ItemCatalogueFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
