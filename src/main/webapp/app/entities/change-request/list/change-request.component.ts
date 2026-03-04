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

// 👇 AQUÍ ESTÁ EL IMPORT CLAVE PARA EL CANDADO DE PERMISOS 👇
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  selector: 'jhi-change-request',
  templateUrl: './change-request.component.html',
  // 👇 AQUÍ SE DECLARA LA DIRECTIVA PARA QUE EL HTML LA PUEDA USAR 👇
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

  // --- CONFIGURACIÓN PRINCIPAL ---
  serverFetchSize = 500; // Traemos los últimos 500
  pageSize = 10; // Mostramos de 10 en 10

  // SEÑAL DE PÁGINA
  page = signal(1);

  // --- SEÑALES DE DATOS Y FILTROS ---
  changeRequests = signal<IChangeRequest[]>([]); // Datos crudos del backend
  isLoading = false;

  searchTerm = signal('');
  statusFilter = signal('');
  startDateFilter = signal<string | null>(null);
  endDateFilter = signal<string | null>(null);

  // --- 1. LÓGICA DE FILTRADO (COMPUTED) ---
  requestsFiltrados = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const status = this.statusFilter();
    const start = this.startDateFilter();
    const end = this.endDateFilter();

    return this.changeRequests().filter(req => {
      // A. Texto
      const matchesText =
        !term ||
        req.title?.toLowerCase().includes(term) ||
        req.departamento?.toLowerCase().includes(term) ||
        req.description?.toLowerCase().includes(term);

      // B. Estado
      const matchesStatus = !status || req.status === status;

      // C. Fechas
      let matchesDate = true;
      if (req.createdDate && (start || end)) {
        const recordDate = new Date(req.createdDate.toString());
        if (start) {
          matchesDate = matchesDate && recordDate >= new Date(start);
        }
        if (end) {
          const endDateObj = new Date(end);
          endDateObj.setHours(23, 59, 59, 999);
          matchesDate = matchesDate && recordDate <= endDateObj;
        }
      }

      return matchesText && matchesStatus && matchesDate;
    });
  });

  // --- 2. LÓGICA DE VISIBILIDAD (PAGINACIÓN CLIENTE) ---
  requestsVisibles = computed(() => {
    // Usamos this.page() porque es una señal
    const startIndex = (this.page() - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.requestsFiltrados().slice(startIndex, endIndex);
  });

  // --- FUNCIONES DE CONTROL ---
  onSearch(val: string) {
    this.searchTerm.set(val);
    this.page.set(1);
  }

  onStatusChange(val: string) {
    this.statusFilter.set(val);
    this.page.set(1);
  }

  // ... código existente ...

  onDateChange(type: 'start' | 'end', e: any) {
    const val = e.target.value;
    type === 'start' ? this.startDateFilter.set(val) : this.endDateFilter.set(val);
    this.page.set(1);
  }

  limpiarFechas(inputInicio: HTMLInputElement, inputFin: HTMLInputElement): void {
    // 1. Limpiar visualmente las cajitas de texto HTML
    inputInicio.value = '';
    inputFin.value = '';

    // 2. Resetear las señales (Signals) a null
    this.startDateFilter.set(null);
    this.endDateFilter.set(null);

    // 3. Volver a la página 1
    this.page.set(1);

    // NOTA: No hace falta llamar a this.load() ni nada más.
    // Como usas 'computed', al cambiar la señal, la lista se actualiza sola.
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
    // 1. Verificamos si hay un orden en la URL (ej: clickeaste una columna)
    const sortParam = params.get(SORT);

    if (sortParam) {
      // Si el usuario pidió un orden específico, lo respetamos
      this.sortState.set(this.sortService.parseSortParam(sortParam));
    } else {
      // Si la URL está limpia (inicio), FORZAMOS 'id' DESCENDENTE (Lo nuevo arriba)
      this.sortState.set({ predicate: 'id', order: 'desc' });
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.changeRequests.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IChangeRequest[] | null): IChangeRequest[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      page: 0,
      size: this.serverFetchSize,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.changeRequestService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
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
