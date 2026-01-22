import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFileRecord } from '../file-record.model';
import { FileRecordService } from '../service/file-record.service';

const fileRecordResolve = (route: ActivatedRouteSnapshot): Observable<null | IFileRecord> => {
  const id = route.params.id;
  if (id) {
    return inject(FileRecordService)
      .find(id)
      .pipe(
        mergeMap((fileRecord: HttpResponse<IFileRecord>) => {
          if (fileRecord.body) {
            return of(fileRecord.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default fileRecordResolve;
