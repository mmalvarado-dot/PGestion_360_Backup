import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import dayjs from 'dayjs/esm';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackingRecord, NewTrackingRecord } from '../tracking-record.model';

export interface ITrackingStats {
  id: number;
  nombre: string;
  total: number;
}
export type EntityResponseType = HttpResponse<ITrackingRecord>;
export type EntityArrayResponseType = HttpResponse<ITrackingRecord[]>;

@Injectable({ providedIn: 'root' })
export class TrackingRecordService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracking-records');

  create(trackingRecord: NewTrackingRecord): Observable<EntityResponseType> {
    return this.http
      .post<ITrackingRecord>(this.resourceUrl, trackingRecord, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackingRecord: ITrackingRecord): Observable<EntityResponseType> {
    return this.http
      .put<ITrackingRecord>(`${this.resourceUrl}/${trackingRecord.id}`, trackingRecord, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITrackingRecord>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITrackingRecord[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  findByRequestId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<ITrackingRecord[]>(`${this.resourceUrl}/request/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  // --- MÉTODOS MODIFICADOS PARA ACEPTAR FILTROS ---

  getStatsByDepartment(req?: any): Observable<HttpResponse<ITrackingStats[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITrackingStats[]>(`${this.resourceUrl}/stats/departments`, { params: options, observe: 'response' });
  }

  getStatsByUser(req?: any): Observable<HttpResponse<ITrackingStats[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITrackingStats[]>(`${this.resourceUrl}/stats/users`, { params: options, observe: 'response' });
  }

  // ------------------------------------------------

  protected convertResponseFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.changeDate = res.body.changeDate ? dayjs(res.body.changeDate) : undefined;
    }
    return res;
  }

  protected convertResponseArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach(tr => (tr.changeDate = tr.changeDate ? dayjs(tr.changeDate) : undefined));
    }
    return res;
  }
}
