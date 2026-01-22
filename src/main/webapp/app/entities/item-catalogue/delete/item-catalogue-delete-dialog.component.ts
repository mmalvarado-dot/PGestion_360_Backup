import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IItemCatalogue } from '../item-catalogue.model';
import { ItemCatalogueService } from '../service/item-catalogue.service';

@Component({
  templateUrl: './item-catalogue-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ItemCatalogueDeleteDialogComponent {
  itemCatalogue?: IItemCatalogue;

  protected itemCatalogueService = inject(ItemCatalogueService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.itemCatalogueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
