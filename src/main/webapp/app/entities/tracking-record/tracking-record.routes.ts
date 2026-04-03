import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DESC } from 'app/config/navigation.constants';
import TrackingRecordResolve from './route/tracking-record-routing-resolve.service';

const trackingRecordRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tracking-record.component').then(m => m.TrackingRecordComponent),
    data: {
      // CORRECCIÓN: Usamos 'change_date' (con guion bajo) para que la base de datos no falle.
      defaultSort: `change_date,${DESC}`,
      // 🔓 PERMITIDO PARA TODOS LOS LOGUEADOS (Usuarios y Admins)
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tracking-record-detail.component').then(m => m.TrackingRecordDetailComponent),
    resolve: {
      trackingRecord: TrackingRecordResolve,
    },
    data: {
      // 🔓 PERMITIDO PARA TODOS LOS LOGUEADOS (Para que vean el detalle de su historial)
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tracking-record-update.component').then(m => m.TrackingRecordUpdateComponent),
    resolve: {
      trackingRecord: TrackingRecordResolve,
    },
    data: {
      // 🔒 BLOQUEADO: Solo el Admin puede forzar la creación manual desde la URL
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tracking-record-update.component').then(m => m.TrackingRecordUpdateComponent),
    resolve: {
      trackingRecord: TrackingRecordResolve,
    },
    data: {
      // 🔒 BLOQUEADO: Solo el Admin puede forzar la edición desde la URL
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default trackingRecordRoute;
