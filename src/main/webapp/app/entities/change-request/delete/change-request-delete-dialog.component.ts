import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IChangeRequest } from '../change-request.model';
import { ChangeRequestService } from '../service/change-request.service';

@Component({
  templateUrl: './change-request-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ChangeRequestDeleteDialogComponent {
  changeRequest?: IChangeRequest;

  protected changeRequestService = inject(ChangeRequestService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.changeRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
