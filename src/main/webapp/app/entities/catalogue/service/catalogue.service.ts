import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICatalogue, NewCatalogue } from '../catalogue.model';

export type PartialUpdateCatalogue = Partial<ICatalogue> & Pick<ICatalogue, 'id'>;

export type EntityResponseType = HttpResponse<ICatalogue>;
export type EntityArrayResponseType = HttpResponse<ICatalogue[]>;

@Injectable({ providedIn: 'root' })
export class CatalogueService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/catalogues');

  create(catalogue: NewCatalogue): Observable<EntityResponseType> {
    return this.http.post<ICatalogue>(this.resourceUrl, catalogue, { observe: 'response' });
  }

  update(catalogue: ICatalogue): Observable<EntityResponseType> {
    return this.http.put<ICatalogue>(`${this.resourceUrl}/${this.getCatalogueIdentifier(catalogue)}`, catalogue, { observe: 'response' });
  }

  partialUpdate(catalogue: PartialUpdateCatalogue): Observable<EntityResponseType> {
    return this.http.patch<ICatalogue>(`${this.resourceUrl}/${this.getCatalogueIdentifier(catalogue)}`, catalogue, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICatalogue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICatalogue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCatalogueIdentifier(catalogue: Pick<ICatalogue, 'id'>): number {
    return catalogue.id;
  }

  compareCatalogue(o1: Pick<ICatalogue, 'id'> | null, o2: Pick<ICatalogue, 'id'> | null): boolean {
    return o1 && o2 ? this.getCatalogueIdentifier(o1) === this.getCatalogueIdentifier(o2) : o1 === o2;
  }

  addCatalogueToCollectionIfMissing<Type extends Pick<ICatalogue, 'id'>>(
    catalogueCollection: Type[],
    ...cataloguesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const catalogues: Type[] = cataloguesToCheck.filter(isPresent);
    if (catalogues.length > 0) {
      const catalogueCollectionIdentifiers = catalogueCollection.map(catalogueItem => this.getCatalogueIdentifier(catalogueItem));
      const cataloguesToAdd = catalogues.filter(catalogueItem => {
        const catalogueIdentifier = this.getCatalogueIdentifier(catalogueItem);
        if (catalogueCollectionIdentifiers.includes(catalogueIdentifier)) {
          return false;
        }
        catalogueCollectionIdentifiers.push(catalogueIdentifier);
        return true;
      });
      return [...cataloguesToAdd, ...catalogueCollection];
    }
    return catalogueCollection;
  }
}
