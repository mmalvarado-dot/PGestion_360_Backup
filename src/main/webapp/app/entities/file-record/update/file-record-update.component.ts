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
import { IChangeRequest } from 'app/entities/change-request/change-request.model';
import { ChangeRequestService } from 'app/entities/change-request/service/change-request.service';
import { FileRecordService } from '../service/file-record.service';
import { IFileRecord } from '../file-record.model';
import { FileRecordFormGroup, FileRecordFormService } from './file-record-form.service';

@Component({
  selector: 'jhi-file-record-update',
  templateUrl: './file-record-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FileRecordUpdateComponent implements OnInit {
  isSaving = false;
  fileRecord: IFileRecord | null = null;

  changeRequestsSharedCollection: IChangeRequest[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected fileRecordService = inject(FileRecordService);
  protected fileRecordFormService = inject(FileRecordFormService);
  protected changeRequestService = inject(ChangeRequestService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FileRecordFormGroup = this.fileRecordFormService.createFileRecordFormGroup();

  compareChangeRequest = (o1: IChangeRequest | null, o2: IChangeRequest | null): boolean =>
    this.changeRequestService.compareChangeRequest(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fileRecord }) => {
      this.fileRecord = fileRecord;
      if (fileRecord) {
        this.updateForm(fileRecord);
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
    const fileRecord = this.fileRecordFormService.getFileRecord(this.editForm);
    if (fileRecord.id !== null) {
      this.subscribeToSaveResponse(this.fileRecordService.update(fileRecord));
    } else {
      this.subscribeToSaveResponse(this.fileRecordService.create(fileRecord));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFileRecord>>): void {
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

  protected updateForm(fileRecord: IFileRecord): void {
    this.fileRecord = fileRecord;
    this.fileRecordFormService.resetForm(this.editForm, fileRecord);

    this.changeRequestsSharedCollection = this.changeRequestService.addChangeRequestToCollectionIfMissing<IChangeRequest>(
      this.changeRequestsSharedCollection,
      fileRecord.changeRequest,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.changeRequestService
      .query()
      .pipe(map((res: HttpResponse<IChangeRequest[]>) => res.body ?? []))
      .pipe(
        map((changeRequests: IChangeRequest[]) =>
          this.changeRequestService.addChangeRequestToCollectionIfMissing<IChangeRequest>(changeRequests, this.fileRecord?.changeRequest),
        ),
      )
      .subscribe((changeRequests: IChangeRequest[]) => (this.changeRequestsSharedCollection = changeRequests));
  }
}
