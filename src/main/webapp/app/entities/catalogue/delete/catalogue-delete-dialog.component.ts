import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICatalogue } from '../catalogue.model';
import { CatalogueService } from '../service/catalogue.service';

@Component({
  templateUrl: './catalogue-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CatalogueDeleteDialogComponent {
  catalogue?: ICatalogue;

  protected catalogueService = inject(CatalogueService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.catalogueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
