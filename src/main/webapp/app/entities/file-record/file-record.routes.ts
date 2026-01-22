import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FileRecordResolve from './route/file-record-routing-resolve.service';

const fileRecordRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/file-record.component').then(m => m.FileRecordComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/file-record-detail.component').then(m => m.FileRecordDetailComponent),
    resolve: {
      fileRecord: FileRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/file-record-update.component').then(m => m.FileRecordUpdateComponent),
    resolve: {
      fileRecord: FileRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/file-record-update.component').then(m => m.FileRecordUpdateComponent),
    resolve: {
      fileRecord: FileRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default fileRecordRoute;
