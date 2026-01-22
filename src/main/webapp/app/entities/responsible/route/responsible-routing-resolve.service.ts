import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResponsible } from '../responsible.model';
import { ResponsibleService } from '../service/responsible.service';

const responsibleResolve = (route: ActivatedRouteSnapshot): Observable<null | IResponsible> => {
  const id = route.params.id;
  if (id) {
    return inject(ResponsibleService)
      .find(id)
      .pipe(
        mergeMap((responsible: HttpResponse<IResponsible>) => {
          if (responsible.body) {
            return of(responsible.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default responsibleResolve;
