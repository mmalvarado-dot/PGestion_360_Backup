import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICatalogue } from '../catalogue.model';
import { CatalogueService } from '../service/catalogue.service';
import { CatalogueFormGroup, CatalogueFormService } from './catalogue-form.service';

@Component({
  selector: 'jhi-catalogue-update',
  templateUrl: './catalogue-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CatalogueUpdateComponent implements OnInit {
  isSaving = false;
  catalogue: ICatalogue | null = null;

  protected catalogueService = inject(CatalogueService);
  protected catalogueFormService = inject(CatalogueFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CatalogueFormGroup = this.catalogueFormService.createCatalogueFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ catalogue }) => {
      this.catalogue = catalogue;
      if (catalogue) {
        this.updateForm(catalogue);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const catalogue = this.catalogueFormService.getCatalogue(this.editForm);
    if (catalogue.id !== null) {
      this.subscribeToSaveResponse(this.catalogueService.update(catalogue));
    } else {
      this.subscribeToSaveResponse(this.catalogueService.create(catalogue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICatalogue>>): void {
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

  protected updateForm(catalogue: ICatalogue): void {
    this.catalogue = catalogue;
    this.catalogueFormService.resetForm(this.editForm, catalogue);
  }
}
