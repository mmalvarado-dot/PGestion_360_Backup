import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICatalogue } from 'app/entities/catalogue/catalogue.model';
import { CatalogueService } from 'app/entities/catalogue/service/catalogue.service';
import { IItemCatalogue } from '../item-catalogue.model';
import { ItemCatalogueService } from '../service/item-catalogue.service';
import { ItemCatalogueFormGroup, ItemCatalogueFormService } from './item-catalogue-form.service';

@Component({
  selector: 'jhi-item-catalogue-update',
  templateUrl: './item-catalogue-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ItemCatalogueUpdateComponent implements OnInit {
  isSaving = false;
  itemCatalogue: IItemCatalogue | null = null;

  cataloguesSharedCollection: ICatalogue[] = [];

  protected itemCatalogueService = inject(ItemCatalogueService);
  protected itemCatalogueFormService = inject(ItemCatalogueFormService);
  protected catalogueService = inject(CatalogueService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ItemCatalogueFormGroup = this.itemCatalogueFormService.createItemCatalogueFormGroup();

  compareCatalogue = (o1: ICatalogue | null, o2: ICatalogue | null): boolean => this.catalogueService.compareCatalogue(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemCatalogue }) => {
      this.itemCatalogue = itemCatalogue;
      if (itemCatalogue) {
        this.updateForm(itemCatalogue);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const itemCatalogue = this.itemCatalogueFormService.getItemCatalogue(this.editForm);
    if (itemCatalogue.id !== null) {
      this.subscribeToSaveResponse(this.itemCatalogueService.update(itemCatalogue));
    } else {
      this.subscribeToSaveResponse(this.itemCatalogueService.create(itemCatalogue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IItemCatalogue>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(itemCatalogue: IItemCatalogue): void {
    this.itemCatalogue = itemCatalogue;
    this.itemCatalogueFormService.resetForm(this.editForm, itemCatalogue);

    this.cataloguesSharedCollection = this.catalogueService.addCatalogueToCollectionIfMissing<ICatalogue>(
      this.cataloguesSharedCollection,
      itemCatalogue.catalogue,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.catalogueService
      .query()
      .pipe(map((res: HttpResponse<ICatalogue[]>) => res.body ?? []))
      .pipe(
        map((catalogues: ICatalogue[]) =>
          this.catalogueService.addCatalogueToCollectionIfMissing<ICatalogue>(catalogues, this.itemCatalogue?.catalogue),
        ),
      )
      .subscribe((catalogues: ICatalogue[]) => (this.cataloguesSharedCollection = catalogues));
  }
}
