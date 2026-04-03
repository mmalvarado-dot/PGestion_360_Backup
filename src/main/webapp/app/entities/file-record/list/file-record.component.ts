import { Component, NgZone, OnInit, inject, signal } from '@angular/core';
import { HttpHeaders, HttpResponse, HttpClient } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { IFileRecord } from '../file-record.model';

import { EntityArrayResponseType, FileRecordService } from '../service/file-record.service';
import { FileRecordDeleteDialogComponent } from '../delete/file-record-delete-dialog.component';

@Component({
  selector: 'jhi-file-record',
  templateUrl: './file-record.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, ItemCountComponent],
})
export class FileRecordComponent implements OnInit {
  subscription: Subscription | null = null;
  fileRecords = signal<IFileRecord[]>([]);

  // Variable para guardar el ID que el usuario quiere buscar
  searchRequestId = signal<number | null>(null);

  isLoading = false;

  sortState = sortStateSignal({});

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public readonly router = inject(Router);
  protected readonly fileRecordService = inject(FileRecordService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  // 👇 Inyectamos HttpClient para hacer la petición segura llevando nuestro Token
  private readonly http = inject(HttpClient);

  trackId = (item: IFileRecord): number => this.fileRecordService.getFileRecordIdentifier(item);

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

  delete(fileRecord: IFileRecord): void {
    const modalRef = this.modalService.open(FileRecordDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.fileRecord = fileRecord;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  // --- NUEVO: Función que se ejecuta al darle al botón de buscar ---
  onSearch(): void {
    this.page = 1; // Reiniciamos a la página 1 al buscar
    this.load();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
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
    const sortParam = params.get(SORT) ?? 'id,desc'; // Forzamos los más nuevos arriba
    this.sortState.set(this.sortService.parseSortParam(sortParam));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.fileRecords.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IFileRecord[] | null): IFileRecord[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };

    // --- NUEVO: Si el usuario escribió un ID, se lo mandamos al Backend de Java ---
    const currentSearchId = this.searchRequestId();
    if (currentSearchId) {
      // JHipster usa este formato por defecto para filtrar relaciones
      queryObject['changeRequestId.equals'] = currentSearchId;
    }

    return this.fileRecordService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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

  // 👇 NUEVA FUNCIÓN PARA DESCARGAR O VER ARCHIVOS CON SEGURIDAD 👇
  downloadPhysicalFile(fileId: number, descargar: boolean): void {
    const url = `/api/change-requests/archivo/${fileId}/descargar?descargar=${descargar}`;

    // Hacemos la petición pidiendo un Blob (archivo binario)
    this.http.get(url, { responseType: 'blob', observe: 'response' }).subscribe({
      next: (response: HttpResponse<Blob>) => {
        if (!response.body) return;

        // Creamos una URL temporal para el archivo en la memoria del navegador
        const fileUrl = window.URL.createObjectURL(response.body);

        if (descargar) {
          // Si es descargar, creamos un enlace invisible y lo "clickeamos"
          let fileName = 'archivo';
          const disposition = response.headers.get('content-disposition');
          if (disposition && disposition.indexOf('filename=') !== -1) {
            const matches = /filename="([^"]*)"/.exec(disposition);
            if (matches != null && matches[1]) fileName = matches[1];
          }

          const a = document.createElement('a');
          a.href = fileUrl;
          a.download = fileName;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
        } else {
          // Si es solo "ver", abrimos la URL temporal en una nueva pestaña
          window.open(fileUrl, '_blank');
        }

        // Limpiamos la memoria
        setTimeout(() => window.URL.revokeObjectURL(fileUrl), 1000);
      },
      error: () => {
        alert('Error: No se pudo cargar o encontrar el archivo físico. Verifica la consola del servidor.');
      },
    });
  }
}
