import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'pGestion360App.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'catalogue',
    data: { pageTitle: 'pGestion360App.catalogue.home.title' },
    loadChildren: () => import('./catalogue/catalogue.routes'),
  },
  {
    path: 'item-catalogue',
    data: { pageTitle: 'pGestion360App.itemCatalogue.home.title' },
    loadChildren: () => import('./item-catalogue/item-catalogue.routes'),
  },
  {
    path: 'department',
    data: { pageTitle: 'pGestion360App.department.home.title' },
    loadChildren: () => import('./department/department.routes'),
  },
  {
    path: 'change-request',
    data: { pageTitle: 'pGestion360App.changeRequest.home.title' },
    loadChildren: () => import('./change-request/change-request.routes'),
  },
  {
    path: 'tracking-record',
    data: { pageTitle: 'pGestion360App.trackingRecord.home.title' },
    loadChildren: () => import('./tracking-record/tracking-record.routes'),
  },
  {
    path: 'file-record',
    data: { pageTitle: 'pGestion360App.fileRecord.home.title' },
    loadChildren: () => import('./file-record/file-record.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
