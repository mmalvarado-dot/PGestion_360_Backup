import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IResponsible } from '../responsible.model';
import { ResponsibleService } from '../service/responsible.service';

@Component({
  templateUrl: './responsible-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ResponsibleDeleteDialogComponent {
  responsible?: IResponsible;

  protected responsibleService = inject(ResponsibleService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.responsibleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
