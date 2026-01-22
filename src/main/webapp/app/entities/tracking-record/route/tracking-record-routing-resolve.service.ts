import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackingRecord } from '../tracking-record.model';
import { TrackingRecordService } from '../service/tracking-record.service';

const trackingRecordResolve = (route: ActivatedRouteSnapshot): Observable<null | ITrackingRecord> => {
  const id = route.params.id;
  if (id) {
    return inject(TrackingRecordService)
      .find(id)
      .pipe(
        mergeMap((trackingRecord: HttpResponse<ITrackingRecord>) => {
          if (trackingRecord.body) {
            return of(trackingRecord.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default trackingRecordResolve;
