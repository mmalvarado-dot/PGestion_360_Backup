import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IItemCatalogue } from '../item-catalogue.model';
import { ItemCatalogueService } from '../service/item-catalogue.service';

const itemCatalogueResolve = (route: ActivatedRouteSnapshot): Observable<null | IItemCatalogue> => {
  const id = route.params.id;
  if (id) {
    return inject(ItemCatalogueService)
      .find(id)
      .pipe(
        mergeMap((itemCatalogue: HttpResponse<IItemCatalogue>) => {
          if (itemCatalogue.body) {
            return of(itemCatalogue.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default itemCatalogueResolve;
