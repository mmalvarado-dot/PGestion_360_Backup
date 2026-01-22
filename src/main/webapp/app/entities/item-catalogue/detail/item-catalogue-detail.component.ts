import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IItemCatalogue } from '../item-catalogue.model';

@Component({
  selector: 'jhi-item-catalogue-detail',
  templateUrl: './item-catalogue-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ItemCatalogueDetailComponent {
  itemCatalogue = input<IItemCatalogue | null>(null);

  previousState(): void {
    window.history.back();
  }
}
