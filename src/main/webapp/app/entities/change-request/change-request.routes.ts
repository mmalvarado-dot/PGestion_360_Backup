import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ChangeRequestResolve from './route/change-request-routing-resolve.service';

const changeRequestRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/change-request.component').then(m => m.ChangeRequestComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/change-request-detail.component').then(m => m.ChangeRequestDetailComponent),
    resolve: {
      changeRequest: ChangeRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/change-request-update.component').then(m => m.ChangeRequestUpdateComponent),
    resolve: {
      changeRequest: ChangeRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/change-request-update.component').then(m => m.ChangeRequestUpdateComponent),
    resolve: {
      changeRequest: ChangeRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default changeRequestRoute;
