import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICatalogue } from '../catalogue.model';
import { CatalogueService } from '../service/catalogue.service';

const catalogueResolve = (route: ActivatedRouteSnapshot): Observable<null | ICatalogue> => {
  const id = route.params.id;
  if (id) {
    return inject(CatalogueService)
      .find(id)
      .pipe(
        mergeMap((catalogue: HttpResponse<ICatalogue>) => {
          if (catalogue.body) {
            return of(catalogue.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default catalogueResolve;
