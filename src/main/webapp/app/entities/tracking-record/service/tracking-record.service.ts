import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackingRecord, NewTrackingRecord } from '../tracking-record.model';

// Interfaz para los datos de las gráficas
export interface ITrackingStats {
  id: number;
  nombre: string;
  total: number;
}

export type PartialUpdateTrackingRecord = Partial<ITrackingRecord> & Pick<ITrackingRecord, 'id'>;

type RestOf<T extends ITrackingRecord | NewTrackingRecord> = Omit<T, 'changeDate'> & {
  changeDate?: string | null;
};

export type RestTrackingRecord = RestOf<ITrackingRecord>;
export type NewRestTrackingRecord = RestOf<NewTrackingRecord>;
export type PartialUpdateRestTrackingRecord = RestOf<PartialUpdateTrackingRecord>;

export type EntityResponseType = HttpResponse<ITrackingRecord>;
export type EntityArrayResponseType = HttpResponse<ITrackingRecord[]>;

@Injectable({ providedIn: 'root' })
export class TrackingRecordService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracking-records');

  // --- MÉTODOS PARA EL BUSCADOR POR ID DE SOLICITUD ---

  findByRequestId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestTrackingRecord[]>(`${this.resourceUrl}/request/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  // --- MÉTODOS PARA GRÁFICOS (TOPS) ---

  getStatsByDepartment(): Observable<HttpResponse<ITrackingStats[]>> {
    return this.http.get<ITrackingStats[]>(`${this.resourceUrl}/stats/departments`, { observe: 'response' });
  }

  getStatsByResponsible(): Observable<HttpResponse<ITrackingStats[]>> {
    return this.http.get<ITrackingStats[]>(`${this.resourceUrl}/stats/responsibles`, { observe: 'response' });
  }

  // --- MÉTODOS ESTÁNDAR DE JHIPSTER ---

  create(trackingRecord: NewTrackingRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackingRecord);
    return this.http
      .post<RestTrackingRecord>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackingRecord: ITrackingRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackingRecord);
    return this.http
      .put<RestTrackingRecord>(`${this.resourceUrl}/${this.getTrackingRecordIdentifier(trackingRecord)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackingRecord: PartialUpdateTrackingRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackingRecord);
    return this.http
      .patch<RestTrackingRecord>(`${this.resourceUrl}/${this.getTrackingRecordIdentifier(trackingRecord)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackingRecord>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackingRecord[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackingRecordIdentifier(trackingRecord: Pick<ITrackingRecord, 'id'>): number {
    return trackingRecord.id;
  }

  compareTrackingRecord(o1: Pick<ITrackingRecord, 'id'> | null, o2: Pick<ITrackingRecord, 'id'> | null): boolean {
    return o1 && o2 ? this.getTrackingRecordIdentifier(o1) === this.getTrackingRecordIdentifier(o2) : o1 === o2;
  }

  addTrackingRecordToCollectionIfMissing<Type extends Pick<ITrackingRecord, 'id'>>(
    trackingRecordCollection: Type[],
    ...trackingRecordsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackingRecords: Type[] = trackingRecordsToCheck.filter(isPresent);
    if (trackingRecords.length > 0) {
      const trackingRecordCollectionIdentifiers = trackingRecordCollection.map(trackingRecordItem =>
        this.getTrackingRecordIdentifier(trackingRecordItem),
      );
      const trackingRecordsToAdd = trackingRecords.filter(trackingRecordItem => {
        const trackingRecordIdentifier = this.getTrackingRecordIdentifier(trackingRecordItem);
        if (trackingRecordCollectionIdentifiers.includes(trackingRecordIdentifier)) {
          return false;
        }
        trackingRecordCollectionIdentifiers.push(trackingRecordIdentifier);
        return true;
      });
      return [...trackingRecordsToAdd, ...trackingRecordCollection];
    }
    return trackingRecordCollection;
  }

  protected convertDateFromClient<T extends ITrackingRecord | NewTrackingRecord | PartialUpdateTrackingRecord>(
    trackingRecord: T,
  ): RestOf<T> {
    return {
      ...trackingRecord,
      changeDate: trackingRecord.changeDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackingRecord: RestOf<ITrackingRecord>): ITrackingRecord {
    return {
      ...restTrackingRecord,
      changeDate: restTrackingRecord.changeDate ? dayjs(restTrackingRecord.changeDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOf<ITrackingRecord>>): HttpResponse<ITrackingRecord> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOf<ITrackingRecord>[]>): HttpResponse<ITrackingRecord[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
