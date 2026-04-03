import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChangeRequest, NewChangeRequest } from '../change-request.model';

export type PartialUpdateChangeRequest = Partial<IChangeRequest> & Pick<IChangeRequest, 'id'>;

type RestOf<T extends IChangeRequest | NewChangeRequest> = Omit<T, 'createdDate' | 'updatedDate' | 'fechaEntrega'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
  fechaEntrega?: string | null;
};

export type RestChangeRequest = RestOf<IChangeRequest>;
export type NewRestChangeRequest = RestOf<NewChangeRequest>;
export type PartialUpdateRestChangeRequest = RestOf<PartialUpdateChangeRequest>;

export type EntityResponseType = HttpResponse<IChangeRequest>;
export type EntityArrayResponseType = HttpResponse<IChangeRequest[]>;

@Injectable({ providedIn: 'root' })
export class ChangeRequestService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/change-requests');

  // ============================================================================
  // NUEVO METODO: Este es el mensajero que lleva el archivo físico a Java
  // ============================================================================
  uploadFile(id: number, file: File): Observable<HttpResponse<any>> {
    const formData = new FormData();
    formData.append('file', file);
    // Llama a nuestra nueva ruta: POST /api/change-requests/{id}/archivo
    return this.http.post(`${this.resourceUrl}/${id}/archivo`, formData, { observe: 'response' });
  }
  // ============================================================================

  create(changeRequest: NewChangeRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(changeRequest);
    return this.http
      .post<RestChangeRequest>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(changeRequest: IChangeRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(changeRequest);
    return this.http
      .put<RestChangeRequest>(`${this.resourceUrl}/${this.getChangeRequestIdentifier(changeRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(changeRequest: PartialUpdateChangeRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(changeRequest);
    return this.http
      .patch<RestChangeRequest>(`${this.resourceUrl}/${this.getChangeRequestIdentifier(changeRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestChangeRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestChangeRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getChangeRequestIdentifier(changeRequest: Pick<IChangeRequest, 'id'>): number {
    return changeRequest.id;
  }

  compareChangeRequest(o1: Pick<IChangeRequest, 'id'> | null, o2: Pick<IChangeRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getChangeRequestIdentifier(o1) === this.getChangeRequestIdentifier(o2) : o1 === o2;
  }

  addChangeRequestToCollectionIfMissing<Type extends Pick<IChangeRequest, 'id'>>(
    changeRequestCollection: Type[],
    ...changeRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const changeRequests: Type[] = changeRequestsToCheck.filter(isPresent);
    if (changeRequests.length > 0) {
      const changeRequestCollectionIdentifiers = changeRequestCollection.map(changeRequestItem =>
        this.getChangeRequestIdentifier(changeRequestItem),
      );
      const changeRequestsToAdd = changeRequests.filter(changeRequestItem => {
        const changeRequestIdentifier = this.getChangeRequestIdentifier(changeRequestItem);
        if (changeRequestCollectionIdentifiers.includes(changeRequestIdentifier)) {
          return false;
        }
        changeRequestCollectionIdentifiers.push(changeRequestIdentifier);
        return true;
      });
      return [...changeRequestsToAdd, ...changeRequestCollection];
    }
    return changeRequestCollection;
  }

  protected convertDateFromClient<T extends IChangeRequest | NewChangeRequest | PartialUpdateChangeRequest>(changeRequest: T): RestOf<T> {
    return {
      ...changeRequest,
      createdDate: changeRequest.createdDate?.format(DATE_FORMAT) ?? null,
      updatedDate: changeRequest.updatedDate?.format(DATE_FORMAT) ?? null,
      fechaEntrega: changeRequest.fechaEntrega?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restChangeRequest: RestChangeRequest): IChangeRequest {
    return {
      ...restChangeRequest,
      createdDate: restChangeRequest.createdDate ? dayjs(restChangeRequest.createdDate) : undefined,
      updatedDate: restChangeRequest.updatedDate ? dayjs(restChangeRequest.updatedDate) : undefined,
      fechaEntrega: restChangeRequest.fechaEntrega ? dayjs(restChangeRequest.fechaEntrega) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestChangeRequest>): HttpResponse<IChangeRequest> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestChangeRequest[]>): HttpResponse<IChangeRequest[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
