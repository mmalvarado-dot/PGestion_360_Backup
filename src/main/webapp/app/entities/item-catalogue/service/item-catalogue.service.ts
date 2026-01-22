import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IItemCatalogue, NewItemCatalogue } from '../item-catalogue.model';

export type PartialUpdateItemCatalogue = Partial<IItemCatalogue> & Pick<IItemCatalogue, 'id'>;

export type EntityResponseType = HttpResponse<IItemCatalogue>;
export type EntityArrayResponseType = HttpResponse<IItemCatalogue[]>;

@Injectable({ providedIn: 'root' })
export class ItemCatalogueService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/item-catalogues');

  create(itemCatalogue: NewItemCatalogue): Observable<EntityResponseType> {
    return this.http.post<IItemCatalogue>(this.resourceUrl, itemCatalogue, { observe: 'response' });
  }

  update(itemCatalogue: IItemCatalogue): Observable<EntityResponseType> {
    return this.http.put<IItemCatalogue>(`${this.resourceUrl}/${this.getItemCatalogueIdentifier(itemCatalogue)}`, itemCatalogue, {
      observe: 'response',
    });
  }

  partialUpdate(itemCatalogue: PartialUpdateItemCatalogue): Observable<EntityResponseType> {
    return this.http.patch<IItemCatalogue>(`${this.resourceUrl}/${this.getItemCatalogueIdentifier(itemCatalogue)}`, itemCatalogue, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IItemCatalogue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IItemCatalogue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getItemCatalogueIdentifier(itemCatalogue: Pick<IItemCatalogue, 'id'>): number {
    return itemCatalogue.id;
  }

  compareItemCatalogue(o1: Pick<IItemCatalogue, 'id'> | null, o2: Pick<IItemCatalogue, 'id'> | null): boolean {
    return o1 && o2 ? this.getItemCatalogueIdentifier(o1) === this.getItemCatalogueIdentifier(o2) : o1 === o2;
  }

  addItemCatalogueToCollectionIfMissing<Type extends Pick<IItemCatalogue, 'id'>>(
    itemCatalogueCollection: Type[],
    ...itemCataloguesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const itemCatalogues: Type[] = itemCataloguesToCheck.filter(isPresent);
    if (itemCatalogues.length > 0) {
      const itemCatalogueCollectionIdentifiers = itemCatalogueCollection.map(itemCatalogueItem =>
        this.getItemCatalogueIdentifier(itemCatalogueItem),
      );
      const itemCataloguesToAdd = itemCatalogues.filter(itemCatalogueItem => {
        const itemCatalogueIdentifier = this.getItemCatalogueIdentifier(itemCatalogueItem);
        if (itemCatalogueCollectionIdentifiers.includes(itemCatalogueIdentifier)) {
          return false;
        }
        itemCatalogueCollectionIdentifiers.push(itemCatalogueIdentifier);
        return true;
      });
      return [...itemCataloguesToAdd, ...itemCatalogueCollection];
    }
    return itemCatalogueCollection;
  }
}
