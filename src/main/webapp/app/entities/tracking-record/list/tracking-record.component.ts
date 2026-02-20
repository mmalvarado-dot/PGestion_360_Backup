import { Component, NgZone, OnInit, inject, signal } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITrackingRecord } from '../tracking-record.model';
import { EntityArrayResponseType, TrackingRecordService, ITrackingStats } from '../service/tracking-record.service';
import { TrackingRecordDeleteDialogComponent } from '../delete/tracking-record-delete-dialog.component';

@Component({
  selector: 'jhi-tracking-record',
  templateUrl: './tracking-record.component.html',
  styleUrls: ['./tracking-record.component.scss'],
  standalone: true,
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    FormatMediumDatePipe,
    ItemCountComponent,
    NgbCollapseModule,
  ],
})
export class TrackingRecordComponent implements OnInit {
  subscription: Subscription | null = null;
  trackingRecords = signal<ITrackingRecord[]>([]);

  statsDepartments = signal<ITrackingStats[]>([]);
  statsResponsibles = signal<ITrackingStats[]>([]);

  searchRequestId = signal<number | null>(null);
  isStatsCollapsed = signal(true);

  isLoading = false;

  // Inicializamos el estado de ordenamiento
  sortState = sortStateSignal(inject(SortService).parseSortParam('changeDate,desc'));

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public readonly router = inject(Router);
  protected readonly trackingRecordService = inject(TrackingRecordService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: ITrackingRecord): number => this.trackingRecordService.getTrackingRecordIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    // Si hay un ID de búsqueda activo, usamos ese método
    if (this.searchRequestId()) {
      this.searchByRequest();
    } else {
      // Si no, cargamos la lista paginada normal
      this.queryBackend().subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
    }
    // Cargamos estadísticas siempre (opcional, podrías moverlo para que solo cargue al abrir el acordeón)
    // Pero está bien dejarlo aquí si no son pesadas.
    // this.loadStats();
  }

  // Método específico para cargar estadísticas al abrir el panel (Optimización opcional)
  toggleStats(): void {
    this.isStatsCollapsed.update(v => !v);
    if (!this.isStatsCollapsed() && this.statsDepartments().length === 0) {
      this.loadStats();
    }
  }

  loadStats(): void {
    this.trackingRecordService.getStatsByDepartment().subscribe({
      next: res => this.statsDepartments.set(res.body ?? []),
      error: () => console.error('Error cargando stats de departamentos'),
    });

    this.trackingRecordService.getStatsByResponsible().subscribe({
      next: res => this.statsResponsibles.set(res.body ?? []),
      error: () => console.error('Error cargando stats de responsables'),
    });
  }

  searchByRequest(): void {
    const requestId = this.searchRequestId();
    if (requestId) {
      this.isLoading = true;
      this.trackingRecordService.findByRequestId(requestId).subscribe({
        next: (res: EntityArrayResponseType) => {
          this.isLoading = false;
          this.trackingRecords.set(res.body ?? []);
          // Cuando buscamos por ID, asumimos que es una lista pequeña, ajustamos totalItems
          this.totalItems = res.body?.length ?? 0;
        },
        error: () => {
          this.isLoading = false;
          this.trackingRecords.set([]);
        },
      });
    } else {
      // Si borran el ID y dan enter, recargamos todo
      this.load();
    }
  }

  clearSearch(): void {
    this.searchRequestId.set(null);
    // IMPORTANTE: Al limpiar, debemos volver a cargar la paginación normal
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  delete(trackingRecord: ITrackingRecord): void {
    const modalRef = this.modalService.open(TrackingRecordDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.trackingRecord = trackingRecord;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState());
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA] ?? 'changeDate,desc'));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.trackingRecords.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: ITrackingRecord[] | null): ITrackingRecord[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page } = this;
    this.isLoading = true;
    const queryObject: any = {
      page: page - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.trackingRecordService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
