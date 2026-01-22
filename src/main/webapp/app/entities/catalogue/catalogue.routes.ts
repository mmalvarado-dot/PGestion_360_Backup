import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CatalogueResolve from './route/catalogue-routing-resolve.service';

const catalogueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/catalogue.component').then(m => m.CatalogueComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/catalogue-detail.component').then(m => m.CatalogueDetailComponent),
    resolve: {
      catalogue: CatalogueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/catalogue-update.component').then(m => m.CatalogueUpdateComponent),
    resolve: {
      catalogue: CatalogueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/catalogue-update.component').then(m => m.CatalogueUpdateComponent),
    resolve: {
      catalogue: CatalogueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default catalogueRoute;
