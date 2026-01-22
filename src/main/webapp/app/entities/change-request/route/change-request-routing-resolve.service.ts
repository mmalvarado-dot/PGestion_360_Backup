import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IChangeRequest } from '../change-request.model';
import { ChangeRequestService } from '../service/change-request.service';

const changeRequestResolve = (route: ActivatedRouteSnapshot): Observable<null | IChangeRequest> => {
  const id = route.params.id;
  if (id) {
    return inject(ChangeRequestService)
      .find(id)
      .pipe(
        mergeMap((changeRequest: HttpResponse<IChangeRequest>) => {
          if (changeRequest.body) {
            return of(changeRequest.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default changeRequestResolve;
