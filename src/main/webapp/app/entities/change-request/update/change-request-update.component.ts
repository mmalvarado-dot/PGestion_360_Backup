import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable, forkJoin } from 'rxjs';
import { finalize, map, switchMap } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

import { IItemCatalogue } from 'app/entities/item-catalogue/item-catalogue.model';
import { ItemCatalogueService } from 'app/entities/item-catalogue/service/item-catalogue.service';
import { prioridad } from 'app/entities/enumerations/prioridad.model';
import { Impacto } from 'app/entities/enumerations/impacto.model';
import { ChangeRequestService } from '../service/change-request.service';
import { IChangeRequest, IUser } from '../change-request.model';
import { ChangeRequestFormGroup, ChangeRequestFormService } from './change-request-form.service';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import dayjs from 'dayjs/esm';
import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';

// --- IMPORTACIONES DE FILE RECORD ---
import { IFileRecord, NewFileRecord } from 'app/entities/file-record/file-record.model';
import { FileRecordService } from 'app/entities/file-record/service/file-record.service';

// 👇 IMPORTACIÓN DEL SERVICIO DE USUARIOS 👇
import { UserService } from 'app/entities/user/service/user.service';

interface PendingFile {
  file: File;
  base64Content: string;
  contentType: string;
}

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

  departmentsSharedCollection: IDepartment[] = [];
  itemCataloguesSharedCollection: IItemCatalogue[] = [];

  // Lista de usuarios reales
  usersSharedCollection: IUser[] = [];

  // Lista para almacenar los múltiples archivos seleccionados
  selectedFiles: PendingFile[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected changeRequestService = inject(ChangeRequestService);
  protected departmentService = inject(DepartmentService);
  protected changeRequestFormService = inject(ChangeRequestFormService);
  protected itemCatalogueService = inject(ItemCatalogueService);
  protected activatedRoute = inject(ActivatedRoute);

  protected fileRecordService = inject(FileRecordService);

  // Inyectamos el servicio de usuarios
  protected userService = inject(UserService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ChangeRequestFormGroup = this.changeRequestFormService.createChangeRequestFormGroup();

  compareItemCatalogue = (o1: IItemCatalogue | null, o2: IItemCatalogue | null): boolean =>
    this.itemCatalogueService.compareItemCatalogue(o1, o2);

  // Compara usuarios para dejar seleccionado el correcto al editar
  compareUser = (o1: IUser | null, o2: IUser | null): boolean => (o1 && o2 ? o1.id === o2.id : o1 === o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ changeRequest }) => {
      this.changeRequest = changeRequest;
      if (changeRequest) {
        this.updateForm(changeRequest);
      }

      this.loadRelationshipsOptions();
      this.loadDepartments();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    const target = event.target as HTMLInputElement;
    if (target && target.files && target.files.length > 0) {
      Array.from(target.files).forEach(file => {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          const base64Data = e.target.result.split(',')[1];
          this.selectedFiles.push({
            file: file,
            base64Content: base64Data,
            contentType: file.type,
          });
        };
        reader.readAsDataURL(file);
      });
      target.value = '';
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const changeRequest = this.changeRequestFormService.getChangeRequest(this.editForm);

    let saveObservable: Observable<HttpResponse<IChangeRequest>>;

    if (changeRequest.id !== null) {
      saveObservable = this.changeRequestService.update(changeRequest);
    } else {
      saveObservable = this.changeRequestService.create(changeRequest);
    }

    saveObservable
      .pipe(
        switchMap((res: HttpResponse<IChangeRequest>) => {
          const savedChangeRequest = res.body!;

          if (this.selectedFiles.length > 0) {
            const fileSaveObservables = this.selectedFiles.map(pendingFile => {
              const fileRecord: NewFileRecord = {
                id: null,
                fileName: pendingFile.file.name,
                filePath: pendingFile.file.name,
                fileType: pendingFile.contentType,
                content: pendingFile.base64Content,
                contentContentType: pendingFile.contentType,
                changeRequest: savedChangeRequest,
              };

              return this.fileRecordService.create(fileRecord);
            });

            return forkJoin(fileSaveObservables).pipe(map(() => res));
          }

          return Observable.create((observer: any) => {
            observer.next(res);
            observer.complete();
          });
        }),
        finalize(() => this.onSaveFinalize()),
      )
      .subscribe({
        next: () => this.onSaveSuccess(),
        error: () => this.onSaveError(),
      });
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

    this.itemCataloguesSharedCollection = this.itemCatalogueService.addItemCatalogueToCollectionIfMissing<IItemCatalogue>(
      this.itemCataloguesSharedCollection,
      changeRequest.itemCatalogue,
    );

    if (changeRequest.user) {
      this.usersSharedCollection = [changeRequest.user];
    }
  }

  protected loadRelationshipsOptions(): void {
    // 1. Catálogo
    this.itemCatalogueService
      .query()
      .pipe(
        map(res => res.body ?? []),
        map(itemCatalogues =>
          this.itemCatalogueService.addItemCatalogueToCollectionIfMissing<IItemCatalogue>(
            itemCatalogues,
            this.changeRequest?.itemCatalogue,
          ),
        ),
      )
      .subscribe(itemCatalogues => (this.itemCataloguesSharedCollection = itemCatalogues));

    // 👇 2. LLAMADA AL BACKEND CON SOLUCIÓN AL CHOQUE DE INTERFACES 👇
    this.userService
      .query()
      .pipe(
        map(res => res.body ?? []),
        map(users => this.userService.addUserToCollectionIfMissing(users, this.changeRequest?.user as any)),
      )
      .subscribe(users => (this.usersSharedCollection = users as any));
  }

  protected loadDepartments(): void {
    this.departmentService.query().subscribe((res: HttpResponse<IDepartment[]>) => (this.departmentsSharedCollection = res.body ?? []));
  }
}
