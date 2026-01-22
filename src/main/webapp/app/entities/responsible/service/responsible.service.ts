import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResponsible, NewResponsible } from '../responsible.model';

export type PartialUpdateResponsible = Partial<IResponsible> & Pick<IResponsible, 'id'>;

export type EntityResponseType = HttpResponse<IResponsible>;
export type EntityArrayResponseType = HttpResponse<IResponsible[]>;

@Injectable({ providedIn: 'root' })
export class ResponsibleService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/responsibles');

  create(responsible: NewResponsible): Observable<EntityResponseType> {
    return this.http.post<IResponsible>(this.resourceUrl, responsible, { observe: 'response' });
  }

  update(responsible: IResponsible): Observable<EntityResponseType> {
    return this.http.put<IResponsible>(`${this.resourceUrl}/${this.getResponsibleIdentifier(responsible)}`, responsible, {
      observe: 'response',
    });
  }

  partialUpdate(responsible: PartialUpdateResponsible): Observable<EntityResponseType> {
    return this.http.patch<IResponsible>(`${this.resourceUrl}/${this.getResponsibleIdentifier(responsible)}`, responsible, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IResponsible>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResponsible[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResponsibleIdentifier(responsible: Pick<IResponsible, 'id'>): number {
    return responsible.id;
  }

  compareResponsible(o1: Pick<IResponsible, 'id'> | null, o2: Pick<IResponsible, 'id'> | null): boolean {
    return o1 && o2 ? this.getResponsibleIdentifier(o1) === this.getResponsibleIdentifier(o2) : o1 === o2;
  }

  addResponsibleToCollectionIfMissing<Type extends Pick<IResponsible, 'id'>>(
    responsibleCollection: Type[],
    ...responsiblesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const responsibles: Type[] = responsiblesToCheck.filter(isPresent);
    if (responsibles.length > 0) {
      const responsibleCollectionIdentifiers = responsibleCollection.map(responsibleItem => this.getResponsibleIdentifier(responsibleItem));
      const responsiblesToAdd = responsibles.filter(responsibleItem => {
        const responsibleIdentifier = this.getResponsibleIdentifier(responsibleItem);
        if (responsibleCollectionIdentifiers.includes(responsibleIdentifier)) {
          return false;
        }
        responsibleCollectionIdentifiers.push(responsibleIdentifier);
        return true;
      });
      return [...responsiblesToAdd, ...responsibleCollection];
    }
    return responsibleCollection;
  }
}
