import { Component, NgZone, OnInit, inject, signal, computed } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { IChangeRequest } from '../change-request.model';

import { ChangeRequestService, EntityArrayResponseType } from '../service/change-request.service';
import { ChangeRequestDeleteDialogComponent } from '../delete/change-request-delete-dialog.component';

import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  selector: 'jhi-change-request',
  templateUrl: './change-request.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    FormatMediumDatePipe,
    ItemCountComponent,
    HasAnyAuthorityDirective,
  ],
})
export class ChangeRequestComponent implements OnInit {
  subscription: Subscription | null = null;

  // Bandera para gestionar la redirección inicial a la última página
  isFirstLoad = true;

  // Parámetros de paginación y filtros
  pageSize = 10;
  page = signal(1);

  changeRequests = signal<IChangeRequest[]>([]);
  isLoading = false;

  searchTerm = signal('');
  statusFilter = signal('');
  startDateFilter = signal<string | null>(null);
  endDateFilter = signal<string | null>(null);

  requestsVisibles = computed(() => {
    return this.changeRequests();
  });

  // Métodos manejadores de eventos de la vista
  onSearch(val: string): void {
    this.searchTerm.set(val);
    this.resetAndLoad();
  }

  onStatusChange(val: string): void {
    this.statusFilter.set(val);
    this.resetAndLoad();
  }

  onDateChange(type: 'start' | 'end', e: any): void {
    const val = e.target.value;
    type === 'start' ? this.startDateFilter.set(val) : this.endDateFilter.set(val);
    this.resetAndLoad();
  }

  limpiarFechas(inputInicio: HTMLInputElement, inputFin: HTMLInputElement): void {
    inputInicio.value = '';
    inputFin.value = '';
    this.startDateFilter.set(null);
    this.endDateFilter.set(null);
    this.resetAndLoad();
  }

  /**
   * Reinicia a la página 1 si se aplican filtros, o recarga los datos directamente
   * si ya se encuentra en la primera página para evitar redundancia en la navegación.
   */
  resetAndLoad(): void {
    this.isFirstLoad = false;
    if (this.page() === 1) {
      this.load();
    } else {
      this.navigateToPage(1);
    }
  }

  sortState = sortStateSignal({});
  totalItems = 0;

  public readonly router = inject(Router);
  protected readonly changeRequestService = inject(ChangeRequestService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IChangeRequest): number => this.changeRequestService.getChangeRequestIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(changeRequest: IChangeRequest): void {
    const modalRef = this.modalService.open(ChangeRequestDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.changeRequest = changeRequest;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event);
  }

  navigateToPage(page: number): void {
    this.page.set(page);
    this.handleNavigation(page, this.sortState());
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const pageParam = params.get('page');
    if (pageParam) {
      this.page.set(Number(pageParam));
    } else {
      this.page.set(1);
    }

    const sortParam = params.get(SORT);
    if (sortParam) {
      this.sortState.set(this.sortService.parseSortParam(sortParam));
    } else {
      // Ordenamiento ascendente por defecto para mantener el flujo cronológico de las páginas
      this.sortState.set({ predicate: 'id', order: 'asc' });
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);

    // Lógica para saltar a la última página en la primera carga si no hay parámetro 'page' en la URL
    if (this.isFirstLoad) {
      this.isFirstLoad = false;
      if (this.totalItems > 0) {
        const lastPage = Math.ceil(this.totalItems / this.pageSize);
        if (lastPage > 1 && !this.activatedRoute.snapshot.queryParamMap.has('page')) {
          this.navigateToPage(lastPage);
          return;
        }
      }
    }

    this.changeRequests.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IChangeRequest[] | null): IChangeRequest[] {
    if (!data) return [];

    // Invierte el arreglo localmente para mantener el orden descendente visual en la tabla
    if (this.sortState().order === 'asc') {
      return data.reverse();
    }

    return data;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;

    const queryObject: any = {
      page: this.page() - 1,
      size: this.pageSize,
      sort: this.sortService.buildSortParam(this.sortState()),
    };

    if (this.searchTerm() && this.searchTerm().trim() !== '') {
      queryObject.globalSearch = this.searchTerm().trim();
    }

    if (this.statusFilter() && this.statusFilter().trim() !== '') {
      queryObject['status.equals'] = this.statusFilter();
    }

    if (this.startDateFilter()) {
      queryObject['createdDate.greaterThanOrEqual'] = this.startDateFilter() + 'T00:00:00Z';
    }
    if (this.endDateFilter()) {
      queryObject['createdDate.lessThanOrEqual'] = this.endDateFilter() + 'T23:59:59Z';
    }

    return this.changeRequestService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page: page,
      sort: this.sortService.buildSortParam(sortState),
    };
    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
        queryParamsHandling: 'merge',
      });
    });
  }
}
