import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TrackingRecordResolve from './route/tracking-record-routing-resolve.service';

const trackingRecordRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tracking-record.component').then(m => m.TrackingRecordComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tracking-record-detail.component').then(m => m.TrackingRecordDetailComponent),
    resolve: {
      trackingRecord: TrackingRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tracking-record-update.component').then(m => m.TrackingRecordUpdateComponent),
    resolve: {
      trackingRecord: TrackingRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tracking-record-update.component').then(m => m.TrackingRecordUpdateComponent),
    resolve: {
      trackingRecord: TrackingRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default trackingRecordRoute;
