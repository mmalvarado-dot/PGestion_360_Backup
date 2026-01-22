import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IResponsible } from 'app/entities/responsible/responsible.model';
import { ResponsibleService } from 'app/entities/responsible/service/responsible.service';
import { IChangeRequest } from 'app/entities/change-request/change-request.model';
import { ChangeRequestService } from 'app/entities/change-request/service/change-request.service';
import { TrackingRecordService } from '../service/tracking-record.service';
import { ITrackingRecord } from '../tracking-record.model';
import { TrackingRecordFormGroup, TrackingRecordFormService } from './tracking-record-form.service';

@Component({
  selector: 'jhi-tracking-record-update',
  templateUrl: './tracking-record-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TrackingRecordUpdateComponent implements OnInit {
  isSaving = false;
  trackingRecord: ITrackingRecord | null = null;

  usersSharedCollection: IUser[] = [];
  responsiblesSharedCollection: IResponsible[] = [];
  changeRequestsSharedCollection: IChangeRequest[] = [];

  protected trackingRecordService = inject(TrackingRecordService);
  protected trackingRecordFormService = inject(TrackingRecordFormService);
  protected userService = inject(UserService);
  protected responsibleService = inject(ResponsibleService);
  protected changeRequestService = inject(ChangeRequestService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TrackingRecordFormGroup = this.trackingRecordFormService.createTrackingRecordFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareResponsible = (o1: IResponsible | null, o2: IResponsible | null): boolean => this.responsibleService.compareResponsible(o1, o2);

  compareChangeRequest = (o1: IChangeRequest | null, o2: IChangeRequest | null): boolean =>
    this.changeRequestService.compareChangeRequest(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackingRecord }) => {
      this.trackingRecord = trackingRecord;
      if (trackingRecord) {
        this.updateForm(trackingRecord);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackingRecord = this.trackingRecordFormService.getTrackingRecord(this.editForm);
    if (trackingRecord.id !== null) {
      this.subscribeToSaveResponse(this.trackingRecordService.update(trackingRecord));
    } else {
      this.subscribeToSaveResponse(this.trackingRecordService.create(trackingRecord));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackingRecord>>): void {
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

  protected updateForm(trackingRecord: ITrackingRecord): void {
    this.trackingRecord = trackingRecord;
    this.trackingRecordFormService.resetForm(this.editForm, trackingRecord);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, trackingRecord.user);
    this.responsiblesSharedCollection = this.responsibleService.addResponsibleToCollectionIfMissing<IResponsible>(
      this.responsiblesSharedCollection,
      trackingRecord.responsible,
    );
    this.changeRequestsSharedCollection = this.changeRequestService.addChangeRequestToCollectionIfMissing<IChangeRequest>(
      this.changeRequestsSharedCollection,
      trackingRecord.changeRequest,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.trackingRecord?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.responsibleService
      .query()
      .pipe(map((res: HttpResponse<IResponsible[]>) => res.body ?? []))
      .pipe(
        map((responsibles: IResponsible[]) =>
          this.responsibleService.addResponsibleToCollectionIfMissing<IResponsible>(responsibles, this.trackingRecord?.responsible),
        ),
      )
      .subscribe((responsibles: IResponsible[]) => (this.responsiblesSharedCollection = responsibles));

    this.changeRequestService
      .query()
      .pipe(map((res: HttpResponse<IChangeRequest[]>) => res.body ?? []))
      .pipe(
        map((changeRequests: IChangeRequest[]) =>
          this.changeRequestService.addChangeRequestToCollectionIfMissing<IChangeRequest>(
            changeRequests,
            this.trackingRecord?.changeRequest,
          ),
        ),
      )
      .subscribe((changeRequests: IChangeRequest[]) => (this.changeRequestsSharedCollection = changeRequests));
  }
}
