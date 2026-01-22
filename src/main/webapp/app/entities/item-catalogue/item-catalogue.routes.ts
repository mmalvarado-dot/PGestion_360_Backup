import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ItemCatalogueResolve from './route/item-catalogue-routing-resolve.service';

const itemCatalogueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/item-catalogue.component').then(m => m.ItemCatalogueComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/item-catalogue-detail.component').then(m => m.ItemCatalogueDetailComponent),
    resolve: {
      itemCatalogue: ItemCatalogueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/item-catalogue-update.component').then(m => m.ItemCatalogueUpdateComponent),
    resolve: {
      itemCatalogue: ItemCatalogueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/item-catalogue-update.component').then(m => m.ItemCatalogueUpdateComponent),
    resolve: {
      itemCatalogue: ItemCatalogueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default itemCatalogueRoute;
