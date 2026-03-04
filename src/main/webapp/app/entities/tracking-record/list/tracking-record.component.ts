import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { combineLatest, filter, tap } from 'rxjs';
import { NgbModal, NgbCollapseModule, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective, SortService, sortStateSignal } from 'app/shared/sort';
import { ITrackingRecord } from '../tracking-record.model';
import { TrackingRecordService, ITrackingStats } from '../service/tracking-record.service';
import { TrackingRecordDeleteDialogComponent } from '../delete/tracking-record-delete-dialog.component';
import { ITEMS_PER_PAGE, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';

@Component({
  selector: 'jhi-tracking-record',
  templateUrl: './tracking-record.component.html',
  standalone: true,
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, NgbCollapseModule, NgbPaginationModule],
})
export class TrackingRecordComponent implements OnInit {
  trackingRecords = signal<ITrackingRecord[]>([]);
  statsDepartments = signal<ITrackingStats[]>([]);
  statsUsers = signal<ITrackingStats[]>([]);

  searchRequestId = signal<number | null>(null);
  isStatsCollapsed = true;
  isLoading = false;
  totalItems = 0;
  page = 1;
  itemsPerPage = ITEMS_PER_PAGE;
  sortState = sortStateSignal(inject(SortService).parseSortParam('changeDate,desc'));

  protected trackingRecordService = inject(TrackingRecordService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected modalService = inject(NgbModal);
  protected sortService = inject(SortService);

  ngOnInit(): void {
    combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params]) => (this.page = +(params.get('page') ?? 1))),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.isLoading = true;
    if (this.searchRequestId()) {
      this.trackingRecordService.findByRequestId(this.searchRequestId()!).subscribe({
        next: res => {
          this.trackingRecords.set(res.body ?? []);
          this.isLoading = false;
        },
        error: () => (this.isLoading = false),
      });
    } else {
      const queryObject = {
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sortService.buildSortParam(this.sortState()),
      };
      this.trackingRecordService.query(queryObject).subscribe({
        next: res => {
          this.trackingRecords.set(res.body ?? []);
          this.totalItems = Number(res.headers.get(TOTAL_COUNT_RESPONSE_HEADER));
          this.isLoading = false;
        },
        error: () => (this.isLoading = false),
      });
    }
  }

  toggleStats(): void {
    this.isStatsCollapsed = !this.isStatsCollapsed;
    if (!this.isStatsCollapsed) this.loadStats();
  }

  loadStats(): void {
    this.trackingRecordService.getStatsByDepartment().subscribe(res => this.statsDepartments.set(res.body ?? []));
    this.trackingRecordService.getStatsByUser().subscribe(res => this.statsUsers.set(res.body ?? []));
  }

  delete(trackingRecord: ITrackingRecord): void {
    const modalRef = this.modalService.open(TrackingRecordDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.trackingRecord = trackingRecord;
    modalRef.closed.pipe(filter(reason => reason === 'deleted')).subscribe(() => this.load());
  }

  navigateToPage(page: number): void {
    this.page = page;
    this.load();
  }
}
