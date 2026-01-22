import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IResponsible } from 'app/entities/responsible/responsible.model';
import { ResponsibleService } from 'app/entities/responsible/service/responsible.service';
import { IItemCatalogue } from 'app/entities/item-catalogue/item-catalogue.model';
import { ItemCatalogueService } from 'app/entities/item-catalogue/service/item-catalogue.service';
import { prioridad } from 'app/entities/enumerations/prioridad.model';
import { Impacto } from 'app/entities/enumerations/impacto.model';
import { ChangeRequestService } from '../service/change-request.service';
import { IChangeRequest } from '../change-request.model';
import { ChangeRequestFormGroup, ChangeRequestFormService } from './change-request-form.service';

@Component({
  selector: 'jhi-change-request-update',
  templateUrl: './change-request-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ChangeRequestUpdateComponent implements OnInit {
  isSaving = false;
  changeRequest: IChangeRequest | null = null;
  prioridadValues = Object.keys(prioridad);
  impactoValues = Object.keys(Impacto);

  responsiblesSharedCollection: IResponsible[] = [];
  itemCataloguesSharedCollection: IItemCatalogue[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected changeRequestService = inject(ChangeRequestService);
  protected changeRequestFormService = inject(ChangeRequestFormService);
  protected responsibleService = inject(ResponsibleService);
  protected itemCatalogueService = inject(ItemCatalogueService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ChangeRequestFormGroup = this.changeRequestFormService.createChangeRequestFormGroup();

  compareResponsible = (o1: IResponsible | null, o2: IResponsible | null): boolean => this.responsibleService.compareResponsible(o1, o2);

  compareItemCatalogue = (o1: IItemCatalogue | null, o2: IItemCatalogue | null): boolean =>
    this.itemCatalogueService.compareItemCatalogue(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ changeRequest }) => {
      this.changeRequest = changeRequest;
      if (changeRequest) {
        this.updateForm(changeRequest);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('pGestion360App.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const changeRequest = this.changeRequestFormService.getChangeRequest(this.editForm);
    if (changeRequest.id !== null) {
      this.subscribeToSaveResponse(this.changeRequestService.update(changeRequest));
    } else {
      this.subscribeToSaveResponse(this.changeRequestService.create(changeRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChangeRequest>>): void {
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

  protected updateForm(changeRequest: IChangeRequest): void {
    this.changeRequest = changeRequest;
    this.changeRequestFormService.resetForm(this.editForm, changeRequest);

    this.responsiblesSharedCollection = this.responsibleService.addResponsibleToCollectionIfMissing<IResponsible>(
      this.responsiblesSharedCollection,
      changeRequest.responsible,
    );
    this.itemCataloguesSharedCollection = this.itemCatalogueService.addItemCatalogueToCollectionIfMissing<IItemCatalogue>(
      this.itemCataloguesSharedCollection,
      changeRequest.itemCatalogue,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.responsibleService
      .query()
      .pipe(map((res: HttpResponse<IResponsible[]>) => res.body ?? []))
      .pipe(
        map((responsibles: IResponsible[]) =>
          this.responsibleService.addResponsibleToCollectionIfMissing<IResponsible>(responsibles, this.changeRequest?.responsible),
        ),
      )
      .subscribe((responsibles: IResponsible[]) => (this.responsiblesSharedCollection = responsibles));

    this.itemCatalogueService
      .query()
      .pipe(map((res: HttpResponse<IItemCatalogue[]>) => res.body ?? []))
      .pipe(
        map((itemCatalogues: IItemCatalogue[]) =>
          this.itemCatalogueService.addItemCatalogueToCollectionIfMissing<IItemCatalogue>(
            itemCatalogues,
            this.changeRequest?.itemCatalogue,
          ),
        ),
      )
      .subscribe((itemCatalogues: IItemCatalogue[]) => (this.itemCataloguesSharedCollection = itemCatalogues));
  }
}
